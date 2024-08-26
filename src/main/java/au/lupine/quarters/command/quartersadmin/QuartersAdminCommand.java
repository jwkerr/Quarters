package au.lupine.quarters.command.quartersadmin;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.command.quartersadmin.method.*;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.PermLevel;
import au.lupine.quarters.object.state.QuarterType;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class QuartersAdminCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            QuartersMessaging.sendErrorMessage(sender, StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
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
            case "delete" -> new AdminDeleteMethod(sender, args).execute();
            case "evict" -> new AdminEvictMethod(sender, args).execute();
            case "meta" -> new AdminMetaMethod(sender, args).execute();
            case "port" -> new AdminPortMethod(sender, args).execute();
            case "sell" -> new AdminSellMethod(sender, args).execute();
            case "set" -> new AdminSetArgument(sender, args).execute();
            case "toggle" -> new AdminToggleArgument(sender, args).execute();
            case "trust" -> new AdminTrustArgument(sender, args).execute();
            case "reload" -> new ReloadArgument(sender, args).execute();
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Stream<String> stream = switch (args.length) {
            case 1 -> Stream.of("delete", "evict", "meta", "port", "sell", "set", "toggle", "trust", "reload");
            case 2 -> switch (args[0]) {
                case "port" -> Stream.of("true", "false");
                case "sell" -> Stream.of("{price}", "cancel");
                case "set" -> Stream.of("anchor", "colour", "name", "owner", "perm", "type");
                case "toggle" -> Stream.of("embassy");
                case "trust" -> Stream.of("add", "clear", "remove");
                case "reload" -> Stream.of("config");
                default -> null;
            };
            case 3 -> switch (args[0]) {
                case "set" -> switch (args[1]) {
                    case "colour" -> Stream.of("{hex}", "{r}");
                    case "name" -> Stream.of("{name}");
                    case "perm" -> Arrays.stream(ActionType.values()).map(ActionType::getLowerCase);
                    case "type" -> Arrays.stream(QuarterType.values()).map(QuarterType::getLowerCase);
                    default -> null;
                };
                default -> null;
            };
            case 4 -> switch (args[0]) {
                case "set" -> switch (args[1]) {
                    case "colour" -> Stream.of("{g}");
                    case "perm" -> Arrays.stream(PermLevel.values()).map(PermLevel::getLowerCase);
                    default -> null;
                };
                default -> null;
            };
            case 5 -> switch (args[0]) {
                case "set" -> switch (args[1]) {
                    case "colour" -> Stream.of("{b}");
                    case "perm" -> Stream.of("true", "false");
                    default -> null;
                };
                default -> null;
            };
            default -> null;
        };

        if (stream == null) return null;

        return stream
                .filter(s -> s.startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
