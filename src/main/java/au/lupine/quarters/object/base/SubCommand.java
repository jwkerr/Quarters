package au.lupine.quarters.object.base;

import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class SubCommand {

    private final String name;
    private final String permission;
    /**
     * If this is true and the player is a mayor, they do not need to have the method permission (assuming this is enabled in config)
     */
    public final boolean hasMayorPermBypass;

    /**
     * Constructor for a sub command.
     * @param name The name of the sub command, this is what the player will type to execute it
     * @param permission The permission required to execute this sub command, if null then no permission is required
     */
    public SubCommand(@NotNull String name, @Nullable String permission) {
        this.name = name;
        this.permission = permission;
        this.hasMayorPermBypass = false;
    }

    /**
     * Constructor for a sub command.
     * @param name The name of the sub command, this is what the player will type to execute it
     * @param permission The permission required to execute this sub command, if null then no permission is required
     */
    public SubCommand(@NotNull String name, @Nullable String permission, boolean hasMayorPermBypass) {
        this.name = name;
        this.permission = permission;
        this.hasMayorPermBypass = hasMayorPermBypass;
    }

    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(name)
                .requires(source -> permission == null || source.getSender().hasPermission(permission))
                .executes(context -> run(context.getSource()));
    }

    public abstract void execute(@NotNull CommandSourceStack source);

    protected int run(@NotNull CommandSourceStack source) {
        try {
            execute(source);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage());
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

    public @NotNull Quarter getQuarterAtPlayerOrByUUID(@NotNull Player player, @Nullable String arg) {
        if (arg == null) {
            Quarter quarter = getQuarterAtPlayer(player);
            if (quarter == null) throw new CommandMethodException("quarters.command.quarter.feedback.not_standing_in_quarter");

            return quarter;
        }

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

    public @Nullable Quarter getQuarterAtPlayer(@NotNull Player player) {
        QuarterManager qm = QuarterManager.getInstance();

        return qm.getQuarter(player.getLocation());
    }
}
