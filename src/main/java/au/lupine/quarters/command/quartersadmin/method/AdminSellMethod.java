package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Town;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class AdminSellMethod extends CommandMethod {

    public AdminSellMethod() {
        super("sell", "quarters.command.quartersadmin.sell");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("price", StringArgumentType.word())
                        .suggests((context, builder) -> suggestPrice(context.getSource(), builder))
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

        Town town = quarter.getTown();

        if (argument == null) {
            double defaultSellPrice = TownMetadataManager.getInstance().getDefaultSellPrice(town);
            String formatted = TownyEconomyHandler.getFormattedBalance(defaultSellPrice);

            quarter.setPrice(defaultSellPrice);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.sell.feedback.for_sale", Argument.string("price", formatted));
            return;
        }

        if (argument.equalsIgnoreCase("cancel")) {
            if (!quarter.isForSale()) throw new CommandMethodException("quarters.command.quarters.sell.feedback.not_for_sale");

            quarter.setPrice(null);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.sell.feedback.not_for_sale_success");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(argument);
        } catch (NumberFormatException e) {
            throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID);
        }

        if (price < 0) throw new CommandMethodException("quarters.command.feedback.price_not_negative");

        String formatted = TownyEconomyHandler.getFormattedBalance(price);

        quarter.setPrice(price);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.sell.feedback.for_sale", Argument.string("price", formatted));
    }

    private @NotNull CompletableFuture<Suggestions> suggestPrice(@NotNull CommandSourceStack source, @NotNull SuggestionsBuilder builder) {
        if (!(source.getSender() instanceof Player player)) return suggestStrings(builder, "cancel");

        Quarter quarter = getQuarterAtPlayerOrNull(player);
        if (quarter == null) return suggestStrings(builder, "cancel");

        double defaultSellPrice = TownMetadataManager.getInstance().getDefaultSellPrice(quarter.getTown());

        return suggestStrings(builder, "cancel", Double.toString(defaultSellPrice));
    }
}
