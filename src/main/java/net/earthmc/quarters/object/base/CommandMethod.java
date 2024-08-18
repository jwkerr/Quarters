package net.earthmc.quarters.object.base;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class CommandMethod {

    public final CommandSender sender;
    public final String[] args;
    public final String permission;

    /**
     * If this is true and the player is a mayor, they do not need to have the method permission (assuming this is enabled in config)
     */
    public final boolean hasMayorPermBypass;

    public CommandMethod(CommandSender sender, String[] args, String permission) {
        this.sender = sender;
        this.args = args;
        this.permission = permission;
        this.hasMayorPermBypass = false;

        checkPermOrThrow();
    }

    public CommandMethod(CommandSender sender, String[] args, String permission, boolean hasMayorPermBypass) {
        this.sender = sender;
        this.args = args;
        this.permission = permission;
        this.hasMayorPermBypass = hasMayorPermBypass;

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

        Player player = getSenderAsPlayerOrNull();
        if (player != null && hasMayorPermBypass && ConfigManager.doMayorsBypassCertainElevatedPerms()) {
            Resident resident = TownyAPI.getInstance().getResident(player);
            Quarters.logInfo("yippee");
            if (resident == null) return;
            Quarters.logInfo("yippee 2");
            if (resident.isMayor()) {
                Quarters.logInfo("yippee 3");
                return;
            }
        }

        if (!sender.hasPermission(permission)) throw new CommandMethodException("You do not have permission to perform this method");
    }

    public Player getSenderAsPlayerOrThrow() {
        if (!(sender instanceof Player player)) throw new CommandMethodException("Only players can use this command");
        return player;
    }

    public @Nullable Player getSenderAsPlayerOrNull() {
        if (!(sender instanceof Player player)) return null;
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
