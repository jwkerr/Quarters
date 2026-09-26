package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import au.lupine.quarters.object.state.QuarterType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class SetTypeMethod extends CommandMethod {

    public SetTypeMethod() {
        super("type", "quarters.command.quarters.set.type", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(QuarterType.values()).map(QuarterType::getLowerCase).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("type", String.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("quarters.command.quarters.set.type.feedback.no_type");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String typeName) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        QuarterType type;
        try {
            type = QuarterType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("quarters.command.quarters.set.type.feedback.invalid_type");
        }

        quarter.setType(type);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(
                player,
                "quarters.command.quarters.set.type.feedback.success",
                Argument.string("type", quarter.getType().getCommonName())
        );
    }
}
