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
        throw new CommandMethodException("You did not specify a price");
    }

    private void execute(@NotNull CommandSourceStack source, double price) {
        Player player = getSenderAsPlayerOrThrow(source);

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        if (price < 0) throw new CommandMethodException("Price must be greater than or equal to 0");

        TownMetadataManager.getInstance().setDefaultSellPrice(town, price);

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed the default quarter sale price of " + town.getName() + " to " + price);
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "has changed the default quarter sale price of " + town.getName() + " to " + price, null);
    }
}
