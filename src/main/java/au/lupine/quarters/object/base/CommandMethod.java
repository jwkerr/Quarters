package au.lupine.quarters.object.base;

import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.command.quarters.QuartersCommand;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * All command methods and arguments extend this class.
 * <p>
 * This is a good place to put anything that will be repeated commonly in command methods. When writing a new command method make sure to:
 * <li>Put your new permission node in the correct alphabetical position in the plugin.yml as well as in any wildcard permissions it may be part of
 * <li>Register your method to the command and/or argument it is part of
 * <li>Add your method name/arguments to the tab completer in its parent command class such as {@link QuartersCommand QuartersCommand}, this should also be done in an alphabetical manner
 */
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

    /**
     * @param hasMayorPermBypass See {@link ConfigManager#doMayorsBypassCertainElevatedPerms()}
     */
    public CommandMethod(CommandSender sender, String[] args, String permission, boolean hasMayorPermBypass) {
        this.sender = sender;
        this.args = args;
        this.permission = permission;
        this.hasMayorPermBypass = hasMayorPermBypass;

        checkPermOrThrow();
    }

    public abstract void execute();

    /**
     * @return The same args but with index 0 removed to "readjust" to 0-indexing again
     */
    public static String[] removeFirstArgument(String[] args) {
        final int length = args.length;
        String[] newArgs = new String[length - 1];
        System.arraycopy(args, 1, newArgs, 0, length - 1);

        return newArgs;
    }

    /**
     * This will throw if the user does not have this command method's {@link #permission} or if they can't bypass it with {@link #hasMayorPermBypass} given their role and server config
     */
    private void checkPermOrThrow() {
        if (permission == null) return;

        Player player = getSenderAsPlayerOrNull();
        if (player != null && hasMayorPermBypass && ConfigManager.doMayorsBypassCertainElevatedPerms()) {
            Resident resident = TownyAPI.getInstance().getResident(player);
            if (resident == null) return;
            if (resident.isMayor()) return;
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

    public @Nullable String getArgOrNull(int index) {
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

    public @Nullable Quarter getQuarterAtPlayerOrNull(Player player) {
        QuarterManager qm = QuarterManager.getInstance();

        return qm.getQuarter(player.getLocation());
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
