package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.RentManager;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RentMethod extends CommandMethod {

    public RentMethod() {
        super("rent", "quarters.command.quarters.rent", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("price", StringArgumentType.word())
                        .suggests((context, builder) -> suggestRentPrice(context.getSource(), builder))
                        .executes(context -> run(context.getSource(), context.getArgument("price", String.class))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        execute(source, null);
    }

    private int run(@NotNull CommandSourceStack source, @Nullable String argument) {
        try {
            execute(source, argument);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    private void execute(@NotNull CommandSourceStack source, @Nullable String argument) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException("quarters.command.feedback.quarter_not_part_of_your_town");
        if (quarter.hasOwner() && !quarter.isPlayerOwner(player)) throw new CommandMethodException("quarters.command.quarters.rent.feedback.occupied");
        Town town = quarter.getTown();

        if (argument == null) {
            double defaultRentPrice = TownMetadataManager.getInstance().getDefaultRentPrice(town);
            validateRentPrice(defaultRentPrice);
            String formatted = TownyEconomyHandler.getFormattedBalance(defaultRentPrice);

            quarter.setRentPrice(defaultRentPrice);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.rent.feedback.for_rent", Argument.string("price", formatted));
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.rent.feedback.town.for_rent", player.getLocation(), Argument.string("price", formatted));
            return;
        }

        if (argument.equalsIgnoreCase("cancel")) {
            if (!quarter.isForRent()) throw new CommandMethodException("quarters.command.quarters.rent.feedback.not_for_rent");

            quarter.setRentPrice(null);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.rent.feedback.not_for_rent_success");
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.rent.feedback.town.not_for_rent", player.getLocation());
            return;
        }

        double price;
        try {
            price = Double.parseDouble(argument);
        } catch (NumberFormatException e) {
            throw new CommandMethodException("quarters.command.feedback.invalid_argument");
        }

        validateRentPrice(price);

        String formatted = TownyEconomyHandler.getFormattedBalance(price);

        quarter.setRentPrice(price);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.rent.feedback.for_rent", Argument.string("price", formatted));
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.rent.feedback.town.for_rent", player.getLocation(), Argument.string("price", formatted));
    }

    public static void validateRentPrice(double price) {
        if (!Double.isFinite(price)) throw new CommandMethodException("quarters.command.feedback.invalid_argument");

        double minimumPrice = RentManager.getMinimumPrice();
        if (price < minimumPrice) {
            throw new CommandMethodException(
                    "quarters.command.quarters.rent.feedback.price_below_minimum",
                    Argument.string("price", TownyEconomyHandler.getFormattedBalance(minimumPrice))
            );
        }

        double maximumPrice = RentManager.getMaximumPrice();
        if (price > maximumPrice) {
            throw new CommandMethodException(
                    "quarters.command.quarters.rent.feedback.price_above_maximum",
                    Argument.string("price", TownyEconomyHandler.getFormattedBalance(maximumPrice))
            );
        }
    }
}
