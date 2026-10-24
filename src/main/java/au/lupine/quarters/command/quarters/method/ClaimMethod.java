package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.event.ownership.QuarterClaimEvent;
import au.lupine.quarters.api.event.ownership.QuarterPreClaimEvent;
import au.lupine.quarters.listener.RentListener;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClaimMethod extends CommandMethod {

    public ClaimMethod() {
        super("claim", "quarters.command.quarters.claim");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("quarter", StringArgumentType.word())
                        .suggests((context, builder) -> suggestQuarterUUIDs(builder))
                        .executes(context -> run(context.getSource(), context.getArgument("quarter", String.class))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        executeClaim(source, null);
    }

    private int run(@NotNull CommandSourceStack source, @Nullable String quarterArgument) {
        try {
            executeClaim(source, quarterArgument);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    private void executeClaim(@NotNull CommandSourceStack source, @Nullable String quarterArgument) {
        Player player = getSenderAsPlayerOrThrow(source);

        Quarter quarter = getQuarterAtPlayerOrByUUIDOrThrow(player, quarterArgument);

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        canResidentClaimQuarter(resident, quarter);
        if (!callPreClaimEvent(player, resident, quarter)) return;

        settleUnclaimedRentOrThrow(quarter, resident);

        sendClaimConfirmation(player, resident, quarter);
    }

    private boolean callPreClaimEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter) {
        Double price = quarter.getPrice();
        if (price == null) return false;

        QuarterPreClaimEvent preClaimEvent = new QuarterPreClaimEvent(player, resident, quarter, price);
        preClaimEvent.callEvent();
        if (!preClaimEvent.isCancelled()) return true;

        String cancelMessage = preClaimEvent.getCancelMessage();
        if (cancelMessage != null) QuartersMessaging.sendErrorMessage(player, cancelMessage);
        return false;
    }

    private void canResidentClaimQuarter(@NotNull Resident resident, @NotNull Quarter quarter) {
        Double price = quarter.getPrice();
        if (price == null) throw new CommandMethodException("quarters.command.quarters.claim.feedback.not_for_sale");

        if (quarter.isResidentOwner(resident)) throw new CommandMethodException("quarters.command.quarters.claim.feedback.already_owner");

        if (!quarter.isEmbassy() && !quarter.getTown().equals(resident.getTownOrNull())) throw new CommandMethodException("quarters.command.quarters.claim.feedback.not_embassy_or_town");

        if (resident.getAccount().getHoldingBalance() < price) throw new CommandMethodException("quarters.command.quarters.claim.feedback.insufficient_funds");
    }

    private void settleUnclaimedRentOrThrow(@NotNull Quarter quarter, @NotNull Resident resident) {
        if (!quarter.hasUnclaimedRentFor(resident)) return;
        if (resident.hasPermissionNode("quarters.exempt.rent")) {
            quarter.clearUnclaimedRent();
            quarter.save();
            return;
        }
        if (!quarter.hasUnclaimedRentDebt()) {
            // The player reclaimed before the next new-day cycle, so no rent period was missed.
            quarter.clearUnclaimedRent();
            quarter.save();
            return;
        }

        if (!RentListener.collectUnclaimedRent(quarter, resident)) {
            throw new CommandMethodException(
                    "quarters.command.quarters.claim.feedback.unpaid_rent",
                    Argument.string("price", TownyEconomyHandler.getFormattedBalance(quarter.getUnclaimedRentPrice() + quarter.getUnclaimedRentTax()))
            );
        }
    }

    private void sendClaimConfirmation(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter) {
        Double currentPrice = quarter.getPrice();
        if (currentPrice == null) return; // This should never happen given prior state

        String formattedPrice = TownyEconomyHandler.getFormattedBalance(currentPrice);

        if (currentPrice > 0) {
            Confirmation.runOnAccept(() -> {
                    try {
                        canResidentClaimQuarter(resident, quarter);
                        if (!currentPrice.equals(quarter.getPrice())) throw new CommandMethodException("quarters.command.quarters.claim.feedback.price_changed");
                    } catch (CommandMethodException e) {
                        QuartersMessaging.sendErrorMessage(player, e.getMessage(), e.getArguments());
                        return;
                    }

                    String reason = "Quarter " + quarter.getUUID() + " sale";
                    resident.getAccount().withdraw(currentPrice, reason);
                    quarter.getTown().getAccount().deposit(currentPrice, reason);

                    changeOwnerAndSave(player, quarter, resident, currentPrice);

                    QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.claim.feedback.success");
                    QuartersMessaging.sendCommandFeedbackToTown(
                            quarter.getTown(),
                            player,
                            "quarters.command.quarters.claim.feedback.town.paid",
                            player.getLocation(),
                            Argument.string("price", formattedPrice)
                    );
                })
                .setTitle(QuartersMessaging.translate(
                        player,
                        "quarters.command.quarters.claim.confirmation.title",
                        Argument.string("price", formattedPrice)
                ))
                .sendTo(player);
        } else {
            changeOwnerAndSave(player, quarter, resident, currentPrice);

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.claim.feedback.success");
            QuartersMessaging.sendCommandFeedbackToTown(
                    quarter.getTown(),
                    player,
                    "quarters.command.quarters.claim.feedback.town.free",
                    player.getLocation()
            );
        }
    }

    private void changeOwnerAndSave(@NotNull Player player, @NotNull Quarter quarter, @NotNull Resident resident, double price) {
        quarter.setOwner(resident.getUUID());
        if (quarter.hasUnclaimedRent() && !quarter.hasUnclaimedRentFor(resident)) quarter.clearUnclaimedRent();
        quarter.setPrice(null);
        quarter.save();

        new QuarterClaimEvent(player, resident, quarter, price).callEvent();
    }
}
