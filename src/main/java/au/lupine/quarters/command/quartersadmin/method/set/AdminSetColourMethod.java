package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public final class AdminSetColourMethod extends CommandMethod {

    public AdminSetColourMethod() {
        super("colour", "quarters.command.quartersadmin.set.colour");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("hex", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, "#9655FF"))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("hex", String.class)))))
                .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                        .suggests((context, builder) -> suggestStrings(builder, "150"))
                        .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                                .suggests((context, builder) -> suggestStrings(builder, "85"))
                                .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                        .suggests((context, builder) -> suggestStrings(builder, "255"))
                                        .executes(context -> run(context.getSource(), () -> execute(
                                                context.getSource(),
                                                context.getArgument("r", Integer.class),
                                                context.getArgument("g", Integer.class),
                                                context.getArgument("b", Integer.class)
                                        ))))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String hex) {
        setColour(source, parseColourAsHex(hex));
    }

    private void execute(@NotNull CommandSourceStack source, int r, int g, int b) {
        try {
            setColour(source, new Color(r, g, b));
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException(StringConstants.A_NUMBER_YOU_PROVIDED_WAS_INVALID);
        }
    }

    private void setColour(@NotNull CommandSourceStack source, @NotNull Color colour) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_CHANGED_THIS_QUARTERS_COLOUR);
    }

    private Color parseColourAsHex(@NotNull String arg) {
        int hex;
        int length = arg.length();

        try {
            if (length < 6 || length >= 8) {
                throw new Exception();
            } else if (length == 6) {
                hex = Integer.parseInt(arg, 16);
            } else {
                if (arg.charAt(0) == '#') {
                    arg = arg.substring(1, 7);
                } else {
                    throw new Exception();
                }

                hex = Integer.parseInt(arg, 16);
            }
        } catch (Exception e) {
            throw new CommandMethodException(StringConstants.A_NUMBER_YOU_PROVIDED_WAS_INVALID);
        }

        return new Color(hex);
    }
}
