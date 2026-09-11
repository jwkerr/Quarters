package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.QuarterType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class AdminSetTypeMethod extends CommandMethod {

    public AdminSetTypeMethod() {
        super("type", "quarters.command.quartersadmin.set.type");
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
        throw new CommandMethodException("No quarter type provided");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String typeName) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        QuarterType type;
        try {
            type = QuarterType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid quarter type provided");
        }

        quarter.setType(type);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter has been set to type: " + quarter.getType().getCommonName());
    }
}
