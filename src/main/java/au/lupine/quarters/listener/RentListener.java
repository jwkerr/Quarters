package au.lupine.quarters.listener;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.RentCollector;
import au.lupine.quarters.object.state.TaxType;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.Account;
import net.kyori.adventure.audience.Audience;
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
        Map<Resident, RentPayment> rentPayments = new HashMap<>();

        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            if (!quarter.isForRent() || !quarter.hasOwner()) continue;
            Double rent = quarter.getRentPrice();
            if (rent == null || rent <= 0) continue;
            Resident resident = quarter.getOwnerResident();
            if (resident == null) continue;

            double tax = getRentTax(rent, rentSettings);
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

            QuartersMessaging.sendSuccessMessage(resident, "quarters.rent.paid",
                    Argument.string("price", TownyEconomyHandler.getFormattedBalance(rentPayment.totalPaid())),
                    Argument.string("quarters", String.valueOf(rentPayment.quarterCount())));
        }

        for (Map.Entry<RentReceiver, RentCollection> entry : rentCollections.entrySet()) {
            RentReceiver receiver = entry.getKey();
            RentCollection rentCollection = entry.getValue();
            if (receiver.account().deposit(rentCollection.total(), RENT_REASON)) {
                QuartersMessaging.sendSuccessMessage(receiver.audience(), "quarters.rent.collected",
                        Argument.string("price", TownyEconomyHandler.getFormattedBalance(rentCollection.total())),
                        Argument.string("quarters", String.valueOf(rentCollection.quarterCount())));
            }
        }
    }

    private void evictAndNotify(@NotNull Resident resident, @NotNull RentPayment rentPayment) {
        for (Quarter quarter : rentPayment.quarters()) quarter.evictOwner();
        QuartersMessaging.sendErrorMessage(resident, "quarters.rent.could_not_pay",
                Argument.string("quarters", String.valueOf(rentPayment.quarterCount())),
                Argument.string("price", TownyEconomyHandler.getFormattedBalance(rentPayment.totalPaid())));
    }

    private double getRentTax(double rent, @NotNull ConfigManager.RentSettings rentSettings) {
        double tax = rentSettings.taxType == TaxType.PERCENTAGE ? rent * rentSettings.tax / 100 : rentSettings.tax;
        return Math.clamp(tax, rentSettings.minTax, rentSettings.maxTax);
    }

    private @Nullable RentReceiver getRentReceiver(@NotNull Quarter quarter, @NotNull RentCollector collector) {
        Town town = quarter.getTown();
        return switch (collector) {
            case TOWN -> new RentReceiver(town.getUUID(), town.getAccount(), town);
            case MAYOR -> {
                Resident mayor = town.getMayor();
                yield new RentReceiver(mayor.getUUID(), mayor.getAccount(), mayor);
            }
            case NATION -> {
                Nation nation = town.getNationOrNull();
                yield nation == null ? null : new RentReceiver(nation.getUUID(), nation.getAccount(), nation);
            }
            case LEADER -> {
                Nation nation = town.getNationOrNull();
                if (nation == null) yield null;
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
