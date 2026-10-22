package au.lupine.quarters.command.quartersadmin;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.command.quartersadmin.method.*;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class QuartersAdminCommand {

    public static @NotNull LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("quartersadmin")
                .requires(source -> source.getSender().hasPermission("quarters.command.quartersadmin"))
                .executes(context -> {
                    QuartersMessaging.sendErrorMessage(context.getSource().getSender(), "quarters.command.feedback.required_argument_missing");
                    return Command.SINGLE_SUCCESS;
                })
                .then(new AdminDeleteMethod().build())
                .then(new AdminEvictMethod().build())
                .then(new AdminMetaArgument().build())
                .then(new AdminPortMethod().build())
                .then(new AdminSellMethod().build())
                .then(new AdminSetArgument().build())
                .then(new AdminToggleArgument().build())
                .then(new AdminTrustArgument().build())
                .then(new ReloadArgument().build())
                .build();
    }
}
