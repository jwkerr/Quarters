package au.lupine.quarters.command.quarters;

import au.lupine.quarters.command.quarters.method.ClaimMethod;
import au.lupine.quarters.command.quarters.method.InfoMethod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class QuartersCommand {

    public static @NotNull LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("quarters")
                .requires(source -> source.getSender().hasPermission("quarters.command.quarters"))
                .executes(context -> {
                    new InfoMethod(context.getSource().getSender(), new String[0]).execute();
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("claim")
                        .executes(context -> {
                            new ClaimMethod(context.getSource().getSender(), new String[0]).execute();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}
