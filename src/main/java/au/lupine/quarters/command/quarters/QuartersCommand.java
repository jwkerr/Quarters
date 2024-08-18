package au.lupine.quarters.command.quarters;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.command.quarters.method.*;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.PermLevel;
import au.lupine.quarters.object.state.QuarterType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
            case "edit" -> new EditArgument(sender, args).execute();
            case "evict" -> new EvictMethod(sender, args).execute();
            case "fame" -> new FameMethod(sender, args).execute();
            case "here" -> new HereMethod(sender, args).execute();
            case "info" -> new InfoMethod(sender, args).execute();
            case "meow" -> new MeowMethod(sender, args).execute();
            case "pos" -> new PosMethod(sender, args).execute();
            case "selection" -> new SelectionArgument(sender, args).execute();
            case "sell" -> new SellMethod(sender, args).execute();
            case "set" -> new SetArgument(sender, args).execute();
            case "toggle" -> new ToggleArgument(sender, args).execute();
            case "trust" -> new TrustArgument(sender, args).execute();
            case "unclaim" -> new UnclaimMethod(sender, args).execute();
            case "wand" -> new WandMethod(sender, args).execute();
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Stream<String> stream = switch (args.length) {
            case 1 -> Stream.of("claim", "create", "delete", "edit", "evict", "fame", "here", "info", "pos", "selection", "sell", "set", "toggle", "trust", "unclaim", "wand");
            case 2 -> switch (args[0]) {
                case "delete" -> Stream.of("all");
                case "edit" -> Stream.of("addselection", "remove");
                case "pos" -> Stream.of("one", "two");
                case "selection" -> Stream.of("add", "clear", "copy", "paste", "remove");
                case "sell" -> Stream.of("{price}");
                case "set" -> Stream.of("anchor", "colour", "defaultsellprice", "name", "owner", "particlesize", "perm", "type");
                case "toggle" -> Stream.of("constantoutlines", "embassy", "entrynotifications", "sellondelete");
                case "trust" -> Stream.of("add", "clear", "remove");
                default -> null;
            };
            case 3 -> switch (args[0]) {
                case "pos" -> Stream.of("{x}");
                case "set" -> switch (args[1]) {
                    case "colour" -> Stream.of("{r}");
                    case "defaultsellprice" -> Stream.of("{price}");
                    case "name" -> Stream.of("{name}");
                    case "particlesize" -> Stream.of("{float}");
                    case "perm" -> Arrays.stream(ActionType.values()).map(ActionType::getLowerCase);
                    case "type" -> Arrays.stream(QuarterType.values()).map(QuarterType::getLowerCase);
                    default -> null;
                };
                default -> null;
            };
            case 4 -> switch (args[0]) {
                case "pos" -> Stream.of("{y}");
                case "set" -> switch (args[1]) {
                    case "colour" -> Stream.of("{g}");
                    case "perm" -> Arrays.stream(PermLevel.values()).map(PermLevel::getLowerCase);
                    default -> null;
                };
                default -> null;
            };
            case 5 -> switch (args[0]) {
                case "pos" -> Stream.of("{z}");
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
