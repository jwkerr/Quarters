package au.lupine.quarters.object.base;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

public abstract class CommandMethod {

    public final CommandSender sender;
    public final String[] args;
    public final String permission;
    private final String name;
    /**
     * If this is true and the player is a mayor, they do not need to have the method permission (assuming this is enabled in config)
     */
    public final boolean hasMayorPermBypass;

    /**
     * Constructor for a sub command.
     * @param name The name of the sub command, this is what the player will type to execute it
     * @param permission The permission required to execute this sub command, if null then no permission is required
     */
    public CommandMethod(@NotNull String name, @Nullable String permission) {
        this.name = name;
        this.permission = permission;
        this.sender = null;
        this.args = new String[0];
        this.hasMayorPermBypass = false;
    }

    /**
     * Constructor for a sub command.
     * @param name The name of the sub command, this is what the player will type to execute it
     * @param permission The permission required to execute this sub command, if null then no permission is required
     */
    public CommandMethod(@NotNull String name, @Nullable String permission, boolean hasMayorPermBypass) {
        this.name = name;
        this.permission = permission;
        this.sender = null;
        this.args = new String[0];
        this.hasMayorPermBypass = hasMayorPermBypass;
    }

    public CommandMethod(@NotNull CommandSender sender, String[] args, @Nullable String permission) {
        this.sender = sender;
        this.args = args;
        this.permission = permission;
        this.name = "";
        this.hasMayorPermBypass = false;

        checkPermOrThrow();
    }

    public CommandMethod(@NotNull CommandSender sender, String[] args, @Nullable String permission, boolean hasMayorPermBypass) {
        this.sender = sender;
        this.args = args;
        this.permission = permission;
        this.name = "";
        this.hasMayorPermBypass = hasMayorPermBypass;

        checkPermOrThrow();
    }

    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(name)
                .requires(source -> permission == null || source.getSender().hasPermission(permission))
                .executes(context -> run(context.getSource()));
    }

    public void execute(@NotNull CommandSourceStack source) {}

    public void execute() {}

    public static String[] removeFirstArgument(String[] args) {
        final int length = args.length;
        String[] newArgs = new String[length - 1];
        System.arraycopy(args, 1, newArgs, 0, length - 1);

        return newArgs;
    }

    private void checkPermOrThrow() {
        if (permission == null) return;

        Player player = getSenderAsPlayerOrNull();
        if (player != null && hasMayorPermBypass && Quarters.getInstance().config().technical.doMayorsBypassCertainElevatedPerms) {
            Resident resident = TownyAPI.getInstance().getResident(player);
            if (resident == null) return;
            if (resident.isMayor()) return;
        }

        if (!sender.hasPermission(permission)) throw new CommandMethodException("quarters.command.feedback.no_method_permission");
    }

    protected int run(@NotNull CommandSourceStack source) {
        try {
            execute(source);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    protected int run(@NotNull CommandSourceStack source, @NotNull Runnable runnable) {
        try {
            runnable.run();
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    /**
     * Gets the player from the command source stack.
     * @param source The command source stack
     * @throws CommandMethodException if the command source stack is not a player
     * @return The player from the command source stack
     */
    public @NotNull Player getSenderAsPlayerOrThrow(@NotNull CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) throw new CommandMethodException("quarters.command.quarter.feedback.only_players");
        return player;
    }

    public @NotNull Player getSenderAsPlayerOrThrow() {
        if (!(sender instanceof Player player)) throw new CommandMethodException("quarters.command.quarter.feedback.only_players");
        return player;
    }

    public @Nullable Player getSenderAsPlayerOrNull() {
        if (!(sender instanceof Player player)) return null;
        return player;
    }

    public @Nullable String getArgOrNull(int index) {
        try {
            return args[index].toLowerCase(Locale.ROOT);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getArgOrThrow(int index, String throwMessage) {
        try {
            return args[index].toLowerCase(Locale.ROOT);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandMethodException(throwMessage);
        }
    }

    public String getArgOrThrow(int index, String throwMessage, boolean lowerCase) {
        try {
            if (lowerCase) return args[index].toLowerCase(Locale.ROOT);
            return args[index];
        } catch (IndexOutOfBoundsException e) {
            throw new CommandMethodException(throwMessage);
        }
    }

    public String getArgOrDefault(int index, String def) {
        try {
            return args[index].toLowerCase(Locale.ROOT);
        } catch (IndexOutOfBoundsException e) {
            return def;
        }
    }

    public @NotNull Quarter getQuarterAtPlayerOrByUUIDOrThrow(@NotNull Player player, @Nullable String arg) {
        if (arg == null) return getQuarterAtPlayerOrThrow(player);

        UUID uuid;
        try {
            uuid = UUID.fromString(arg);
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("quarters.command.quarter.feedback.invalid_uuid");
        }

        Quarter quarter = QuarterManager.getInstance().getQuarter(uuid);
        if (quarter == null) throw new CommandMethodException("quarters.command.quarter.feedback.no_longer_exists");

        return quarter;
    }

    public @NotNull Quarter getQuarterAtPlayerOrThrow(@NotNull Player player) {
        Quarter quarter = getQuarterAtPlayerOrNull(player);
        if (quarter == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_STANDING_WITHIN_A_QUARTER);

        return quarter;
    }

    public @Nullable Quarter getQuarterAtPlayerOrNull(@NotNull Player player) {
        QuarterManager qm = QuarterManager.getInstance();

        return qm.getQuarter(player.getLocation());
    }

    protected @NotNull CompletableFuture<Suggestions> suggestStrings(@NotNull SuggestionsBuilder builder, @NotNull String... suggestions) {
        String remaining = builder.getRemainingLowerCase();

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase(Locale.ROOT).startsWith(remaining)) builder.suggest(suggestion);
        }

        return builder.buildFuture();
    }

    protected @NotNull CompletableFuture<Suggestions> suggestQuarterUUIDs(@NotNull SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();

        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            String uuid = quarter.getUUID().toString();
            if (uuid.startsWith(remaining)) builder.suggest(uuid);
        }

        return builder.buildFuture();
    }

}
