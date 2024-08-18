package net.earthmc.quarters.object.base;

import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class CommandMethod {

    public final CommandSender sender;
    public final String[] args;
    public final String permission;

    public CommandMethod(CommandSender sender, String[] args, String permission) {
        this.sender = sender;
        this.args = args;
        this.permission = permission;

        checkPermOrThrow();
    }

    public abstract void execute();

    public static String[] removeFirstArgument(String[] args) {
        final int length = args.length;
        String[] newArgs = new String[length - 1];
        System.arraycopy(args, 1, newArgs, 0, length - 1);

        return newArgs;
    }

    private void checkPermOrThrow() {
        if (permission == null) return;
        if (!sender.hasPermission(permission)) throw new CommandMethodException("You do not have permission to perform this method");
    }

    public Player getSenderAsPlayerOrThrow() {
        if (!(sender instanceof Player player)) throw new CommandMethodException("Only players can use this command");
        return player;
    }

    public String getArgOrNull(int index) {
        try {
            return args[index].toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getArgOrThrow(int index, String throwMessage) {
        try {
            return args[index].toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            throw new CommandMethodException(throwMessage);
        }
    }

    public String getArgOrDefault(int index, String def) {
        try {
            return args[index].toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            return def;
        }
    }

    public @NotNull Quarter getQuarterAtPlayerOrThrow(Player player) {
        QuarterManager qm = QuarterManager.getInstance();

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_STANDING_WITHIN_A_QUARTER);

        return quarter;
    }

    public @NotNull Quarter getQuarterAtPlayerOrByUUIDOrThrow(Player player, String arg) {
        if (arg == null) return getQuarterAtPlayerOrThrow(player);

        UUID uuid;
        try {
            uuid = UUID.fromString(arg);
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid quarter UUID provided");
        }

        Quarter quarter = QuarterManager.getInstance().getQuarter(uuid);
        if (quarter == null) throw new CommandMethodException("This quarter no longer exists");

        return quarter;
    }
}
