package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.AdminSellMethod(context.getSource().getSender(), new String[]{context.getArgument("price", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.AdminSellMethod(source.getSender(), new String[0]).execute();
    }

    private @NotNull CompletableFuture<Suggestions> suggestPrice(@NotNull CommandSourceStack source, @NotNull SuggestionsBuilder builder) {
        if (!(source.getSender() instanceof Player player)) return suggestStrings(builder, "cancel");

        Quarter quarter = getQuarterAtPlayerOrNull(player);
        if (quarter == null) return suggestStrings(builder, "cancel");

        double defaultSellPrice = TownMetadataManager.getInstance().getDefaultSellPrice(quarter.getTown());

        return suggestStrings(builder, "cancel", Double.toString(defaultSellPrice));
    }
}
