package net.earthmc.quarters.command.quarters;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.command.quarters.method.*;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuartersCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            new InfoMethod(sender, args).execute();
            return true;
        }

        try {
            parseMethod(sender, args[0].toLowerCase(), CommandMethod.removeFirstArgument(args));
        } catch (CommandMethodException e) {
            QuartersMessaging.sendMessage(sender, e.getComponent());
        }

        return true;
    }

    private void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "claim" -> new ClaimMethod(sender, args).execute();
            case "create" -> new CreateMethod(sender, args).execute();
            case "delete" -> new DeleteMethod(sender, args).execute();
            case "evict" -> new EvictMethod(sender, args).execute();
            case "here" -> new HereMethod(sender, args).execute();
            case "info" -> new InfoMethod(sender, args).execute();
            case "pos" -> new PosMethod(sender, args).execute();
            case "selection" -> new SelectionArgument(sender, args).execute();
            case "sell" -> new SellMethod(sender, args).execute();
            case "set" -> new SetArgument(sender, args).execute();
            case "toggle" -> new ToggleArgument(sender, args).execute();
            case "trust" -> new TrustArgument(sender, args).execute();
            case "unclaim" -> new UnclaimMethod(sender, args).execute();
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
