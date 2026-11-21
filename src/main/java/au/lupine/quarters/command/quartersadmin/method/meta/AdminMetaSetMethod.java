package au.lupine.quarters.command.quartersadmin.method.meta;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class AdminMetaSetMethod extends CommandMethod {

    public AdminMetaSetMethod() {
        super("set", "quarters.command.quartersadmin.meta.set");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("key", StringArgumentType.word())
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes(context -> run(context.getSource(), () -> execute(
                                        context.getSource(),
                                        context.getArgument("key", String.class),
                                        context.getArgument("value", String.class)
                                )))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("quarters.command.quartersadmin.meta.feedback.no_key");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String key, @NotNull String value) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        CustomDataField<?> cdf = quarter.getMetadata(key);
        if (cdf == null) {
            Map<String, CustomDataField<?>> registeredMetadata = TownyUniverse.getInstance().getRegisteredMetadataMap();

            CustomDataField<?> registeredDataField = registeredMetadata.get(key);
            if (registeredDataField == null) throw new CommandMethodException("quarters.command.quartersadmin.meta.set.feedback.unknown_key", Argument.string("key", key));

            cdf = registeredDataField.clone();
        }

        cdf.setValueFromString(value);

        quarter.addMetaData(cdf, true);

        QuartersMessaging.sendSuccessMessage(
                player,
                "quarters.command.quartersadmin.meta.set.feedback.success",
                Argument.string("key", key),
                Argument.string("value", value)
        );
    }
}
