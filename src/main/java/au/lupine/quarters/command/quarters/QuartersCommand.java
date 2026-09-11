package au.lupine.quarters.command.quarters;

import au.lupine.quarters.command.quarters.method.ClaimMethod;
import au.lupine.quarters.command.quarters.legacy_method.InfoMethod;
import au.lupine.quarters.command.quarters.method.CreateMethod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class QuartersCommand {

    public static @NotNull LiteralCommandNode<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("quarters")
                .requires(source -> source.getSender().hasPermission("quarters.command.quarters"))
                .executes(context -> {
                    new InfoMethod(context.getSource().getSender(), new String[0]).execute();
                    return Command.SINGLE_SUCCESS;
                });

        // Sub commands
        root.then(new ClaimMethod().build());
        root.then(new CreateMethod().build());

        return root.build();
    }
}
