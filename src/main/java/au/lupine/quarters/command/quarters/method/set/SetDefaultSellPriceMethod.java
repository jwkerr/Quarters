package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SetDefaultSellPriceMethod extends CommandMethod {

    public SetDefaultSellPriceMethod() {
        super("defaultsellprice", "quarters.command.quarters.set.defaultsellprice", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("price", DoubleArgumentType.doubleArg(0))
                        .suggests((context, builder) -> suggestStrings(builder, "0"))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("price", Double.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("quarters.command.quarters.set.defaultsellprice.feedback.no_price");
    }

    private void execute(@NotNull CommandSourceStack source, double price) {
        Player player = getSenderAsPlayerOrThrow(source);

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        if (price < 0) throw new CommandMethodException("quarters.command.feedback.price_not_negative");

        TownMetadataManager.getInstance().setDefaultSellPrice(town, price);

        QuartersMessaging.sendSuccessMessage(
                player,
                "quarters.command.quarters.set.defaultsellprice.feedback.success",
                Argument.string("town", town.getName()),
                Argument.string("price", Double.toString(price))
        );
        QuartersMessaging.sendCommandFeedbackToTown(
                town,
                player,
                "quarters.command.quarters.set.defaultsellprice.feedback.town",
                null,
                Argument.string("town", town.getName()),
                Argument.string("price", Double.toString(price))
        );
    }
}
