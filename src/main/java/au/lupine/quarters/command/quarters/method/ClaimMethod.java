package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
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

        sendClaimConfirmation(resident, quarter);
    }

    private void canResidentClaimQuarter(@NotNull Resident resident, @NotNull Quarter quarter) {
        Double price = quarter.getPrice();
        if (price == null) throw new CommandMethodException("quarters.command.quarters.claim.feedback.not_for_sale");

        if (quarter.isResidentOwner(resident)) throw new CommandMethodException("quarters.command.quarters.claim.feedback.already_owner");

        if (!quarter.isEmbassy() && !quarter.getTown().equals(resident.getTownOrNull())) throw new CommandMethodException("quarters.command.quarters.claim.feedback.not_embassy_or_town");

        if (resident.getAccount().getHoldingBalance() < price) throw new CommandMethodException("quarters.command.quarters.claim.feedback.insufficient_funds");
    }

    private void sendClaimConfirmation(@NotNull Resident resident, @NotNull Quarter quarter) {
        Double currentPrice = quarter.getPrice();
        if (currentPrice == null) return; // This should never happen given prior state

        Player player = resident.getPlayer();
        if (player == null) return;

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

                    changeOwnerAndSave(quarter, resident);

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
            changeOwnerAndSave(quarter, resident);

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.claim.feedback.success");
            QuartersMessaging.sendCommandFeedbackToTown(
                    quarter.getTown(),
                    player,
                    "quarters.command.quarters.claim.feedback.town.free",
                    player.getLocation()
            );
        }
    }

    private void changeOwnerAndSave(@NotNull Quarter quarter, @NotNull Resident resident) {
        quarter.setOwner(resident.getUUID());
        quarter.setPrice(null);
        quarter.save();
    }
}
