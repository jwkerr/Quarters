package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

public final class SetColourMethod extends CommandMethod {
    private static final Map<String, Color> COLOURS = Map.ofEntries(
            Map.entry("white", new Color(249, 255, 254)),
            Map.entry("light_gray", new Color(157, 157, 151)),
            Map.entry("gray", new Color(71, 79, 82)),
            Map.entry("black", new Color(29, 29, 33)),
            Map.entry("red", new Color(176, 46, 38)),
            Map.entry("orange", new Color(249, 128, 29)),
            Map.entry("yellow", new Color(254, 216, 61)),
            Map.entry("lime", new Color(128, 199, 31)),
            Map.entry("green", new Color(94, 124, 22)),
            Map.entry("cyan", new Color(22, 156, 156)),
            Map.entry("light_blue", new Color(58, 179, 218)),
            Map.entry("blue", new Color(60, 68, 170)),
            Map.entry("purple", new Color(137, 50, 184)),
            Map.entry("magenta", new Color(199, 78, 189)),
            Map.entry("pink", new Color(243, 139, 170)),
            Map.entry("brown", new Color(131, 84, 50))
    );

    public SetColourMethod() {
        super("colour", "quarters.command.quarters.set.colour");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("colour", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, COLOURS.keySet().toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("colour", String.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String colourName) {
        Color colour = COLOURS.get(colourName.toLowerCase(Locale.ROOT));
        if (colour == null) throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID);

        setColour(source, colour);
    }

    private void setColour(@NotNull CommandSourceStack source, @NotNull Color colour) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_CHANGED_THIS_QUARTERS_COLOUR);
    }
}
