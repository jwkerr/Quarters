package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.SelectionType;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PosMethod extends CommandMethod {

    public PosMethod() {
        super("pos", "quarters.command.quarters.pos");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("position", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, "one", "two"))
                        .executes(context -> run(context.getSource(), context.getArgument("position", String.class), 0, 0, 0))
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .suggests((context, builder) -> suggestStrings(builder, "0"))
                                .executes(context -> run(
                                        context.getSource(),
                                        context.getArgument("position", String.class),
                                        context.getArgument("x", Integer.class),
                                        0,
                                        0
                                ))
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .suggests((context, builder) -> suggestStrings(builder, "0"))
                                        .executes(context -> run(
                                                context.getSource(),
                                                context.getArgument("position", String.class),
                                                context.getArgument("x", Integer.class),
                                                context.getArgument("y", Integer.class),
                                                0
                                        ))
                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                .suggests((context, builder) -> suggestStrings(builder, "0"))
                                                .executes(context -> run(
                                                        context.getSource(),
                                                        context.getArgument("position", String.class),
                                                        context.getArgument("x", Integer.class),
                                                        context.getArgument("y", Integer.class),
                                                        context.getArgument("z", Integer.class)
                                                ))))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    private int run(@NotNull CommandSourceStack source, @NotNull String position, int adjustX, int adjustY, int adjustZ) {
        try {
            execute(source, position, adjustX, adjustY, adjustZ);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String position, int adjustX, int adjustY, int adjustZ) {
        Player player = getSenderAsPlayerOrThrow(source);

        SelectionType type = switch (position.toLowerCase()) {
            case "one" -> SelectionType.LEFT;
            case "two" -> SelectionType.RIGHT;
            default -> throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID);
        };

        Location location = player.getLocation();
        location.add(adjustX, adjustY, adjustZ);

        SelectionManager sm = SelectionManager.getInstance();

        if (!sm.isLocationInsideWorldBounds(location)) {
            throw new CommandMethodException(StringConstants.A_NUMBER_YOU_PROVIDED_WAS_INVALID);
        }

        sm.selectPosition(player, location, type);

        QuartersMessaging.sendMessage(player, sm.getSelectedPositionComponent(type, location));
    }
}
