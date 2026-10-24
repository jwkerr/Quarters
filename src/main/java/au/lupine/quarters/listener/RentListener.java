package au.lupine.quarters.listener;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.RentManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.RentCollector;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.Account;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Processes daily quarter-rent payments after Towny's new-day cycle begins. */
public class RentListener implements Listener {

    private static final String RENT_REASON = "Quarters rent";

    @EventHandler
    public void onNewDay(@NotNull NewDayEvent event) {
        ConfigManager.RentSettings rentSettings = Quarters.getInstance().config().quarters.rentSettings;
        processUnclaimedRent();
        Map<Resident, RentPayment> rentPayments = new HashMap<>();

        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            if (!quarter.isForRent() || !quarter.hasOwner()) continue;
            Double rent = quarter.getRentPrice();
            if (rent == null || rent <= 0) continue;
            Resident resident = quarter.getOwnerResident();
            if (resident == null) continue;
            if (resident.hasPermissionNode("quarters.exempt.rent")) continue;

            double tax = RentManager.getTax(rent);
            RentPayment rentPayment = rentPayments.computeIfAbsent(resident, ignored -> new RentPayment());
            rentPayment.add(quarter, getRentReceiver(quarter, rentSettings.collector), rent, rent + tax);
        }

        Map<RentReceiver, RentCollection> rentCollections = new HashMap<>();
        for (Map.Entry<Resident, RentPayment> entry : rentPayments.entrySet()) {
            Resident resident = entry.getKey();
            RentPayment rentPayment = entry.getValue();

            if (resident.getAccount().getHoldingBalance() < rentPayment.totalPaid()
                    || !resident.getAccount().withdraw(rentPayment.totalPaid(), RENT_REASON)) {
                evictAndNotify(resident, rentPayment);
                continue;
            }

            for (RentLine rentLine : rentPayment.lines()) {
                if (rentLine.receiver() != null) {
                    rentCollections.computeIfAbsent(rentLine.receiver(), ignored -> new RentCollection()).add(rentLine.rent(), 1);
                }
            }

            int quarters = rentPayment.quarterCount();
            QuartersMessaging.sendSuccessMessage(resident, "quarters.rent.paid",
                    Argument.string("price", TownyEconomyHandler.getFormattedBalance(rentPayment.totalPaid())),
                    Argument.string("quarters", String.valueOf(quarters)),
                    Argument.tagResolver(Formatter.booleanChoice("plural", quarters == 1))
            );
        }

        for (Map.Entry<RentReceiver, RentCollection> entry : rentCollections.entrySet()) {
            RentReceiver receiver = entry.getKey();
            RentCollection rentCollection = entry.getValue();
            if (receiver.account().deposit(rentCollection.total(), RENT_REASON)) {
                int quarters = rentCollection.quarterCount();
                QuartersMessaging.sendSuccessMessage(receiver.audience(), "quarters.rent.collected",
                        Argument.string("price", TownyEconomyHandler.getFormattedBalance(rentCollection.total())),
                        Argument.string("quarters", String.valueOf(quarters)),
                        Argument.tagResolver(Formatter.booleanChoice("plural", quarters == 1))
                );
            }
        }
    }

    /**
     * Marks rent owed when a player unclaims before the new-day cycle. It is collected only if the
     * former owner later reclaims the quarter, so a player who has permanently left is not charged.
     */
    private void processUnclaimedRent() {
        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            if (!quarter.hasUnclaimedRent() || quarter.hasUnclaimedRentDebt()) continue;

            Resident formerOwner = quarter.getUnclaimedRentOwner();
            if (formerOwner == null) continue;
            if (formerOwner.hasPermissionNode("quarters.exempt.rent")) {
                quarter.clearUnclaimedRent();
                quarter.save();
                continue;
            }

            double rent = quarter.getUnclaimedRentPrice();
            double tax = RentManager.getTax(rent);
            quarter.markUnclaimedRentDue(tax);
        }
    }

    /**
     * Attempts to settle a former owner's recorded rent obligation.
     *
     * @return true when the payment was collected and transferred successfully
     */
    public static boolean collectUnclaimedRent(@NotNull Quarter quarter, @NotNull Resident resident) {
        return collectUnclaimedRent(quarter, resident, Quarters.getInstance().config().quarters.rentSettings);
    }

    private static boolean collectUnclaimedRent(@NotNull Quarter quarter, @NotNull Resident resident, @NotNull ConfigManager.RentSettings rentSettings) {
        if (!quarter.hasUnclaimedRentFor(resident)) return true;

        double rent = quarter.getUnclaimedRentPrice();
        double tax = quarter.getUnclaimedRentTax();
        double total = rent + tax;
        if (resident.getAccount().getHoldingBalance() < total || !resident.getAccount().withdraw(total, RENT_REASON)) return false;

        RentReceiver receiver = getRentReceiver(quarter, rentSettings.collector);
        if (receiver != null) receiver.account().deposit(rent, RENT_REASON);

        quarter.clearUnclaimedRent();
        quarter.save();
        return true;
    }

    private void evictAndNotify(@NotNull Resident resident, @NotNull RentPayment rentPayment) {
        for (Quarter quarter : rentPayment.quarters()) quarter.evictOwner();
        int quarters = rentPayment.quarterCount();
        QuartersMessaging.sendErrorMessage(resident, "quarters.rent.could_not_pay",
                Argument.numeric("quarters", quarters),
                Argument.string("price", TownyEconomyHandler.getFormattedBalance(rentPayment.totalPaid())),
                Argument.tagResolver(Formatter.booleanChoice("plural", quarters == 1))
        );
    }

    private static @Nullable RentReceiver getRentReceiver(@NotNull Quarter quarter, @NotNull RentCollector collector) {
        Town town = quarter.getTown();
        return switch (collector) {
            case TOWN -> new RentReceiver(town.getUUID(), town.getAccount(), town);
            case MAYOR -> {
                Resident mayor = town.getMayor();
                yield new RentReceiver(mayor.getUUID(), mayor.getAccount(), mayor);
            }
            case NATION -> {
                Nation nation = town.getNationOrNull();
                yield nation == null
                        ? new RentReceiver(town.getUUID(), town.getAccount(), town)
                        : new RentReceiver(nation.getUUID(), nation.getAccount(), nation);
            }
            case LEADER -> {
                Nation nation = town.getNationOrNull();
                if (nation == null) yield new RentReceiver(town.getUUID(), town.getAccount(), town);
                Resident leader = nation.getKing();
                yield new RentReceiver(leader.getUUID(), leader.getAccount(), leader);
            }
            case SERVER -> null;
        };
    }

    private record RentLine(@Nullable RentReceiver receiver, double rent) {}

    private record RentReceiver(@NotNull UUID uuid, @NotNull Account account, @NotNull Audience audience) {}

    private static final class RentPayment {
        private final List<RentLine> lines = new ArrayList<>();
        private final List<Quarter> quarters = new ArrayList<>();
        private double totalPaid;

        void add(@NotNull Quarter quarter, @Nullable RentReceiver receiver, double rent, double paid) {
            lines.add(new RentLine(receiver, rent));
            quarters.add(quarter);
            totalPaid += paid;
        }

        @NotNull List<RentLine> lines() { return lines; }
        @NotNull List<Quarter> quarters() { return quarters; }
        double totalPaid() { return totalPaid; }
        int quarterCount() { return quarters.size(); }
    }

    private static final class RentCollection {
        private double total;
        private int quarterCount;

        void add(double rent, int quarters) {
            total += rent;
            quarterCount += quarters;
        }

        double total() { return total; }
        int quarterCount() { return quarterCount; }
    }
}
