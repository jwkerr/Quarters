package au.lupine.quarters.command.quarters;

import au.lupine.quarters.command.quarters.method.*;
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
                    // FIXME: Currently the permission "quarters.command.quarters.info" is ignored here
                    new InfoMethod().execute(context.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(new ClaimMethod().build())
                .then(new CreateMethod().build())
                .then(new DeleteArgument().build())
                .then(new EditArgument().build())
                .then(new EvictMethod().build())
                .then(new FameMethod().build())
                .then(new HereMethod().build())
                .then(new InfoMethod().build())
                .then(new MeowMethod().build())
                .then(new PosMethod().build())
                .then(new SelectionArgument().build())
                .then(new SellMethod().build())
                .then(new SetArgument().build())
                .then(new ToggleArgument().build())
                .then(new TrustArgument().build())
                .then(new UnclaimMethod().build())
                .then(new WandMethod().build())
                .build();
    }
}
