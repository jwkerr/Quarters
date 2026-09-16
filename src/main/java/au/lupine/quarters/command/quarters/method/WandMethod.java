package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class WandMethod extends CommandMethod {

    public WandMethod() {
        super("wand", "quarters.command.quarters.wand");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack) {
        if (ConfigManager.getFreeWandAmount() == 0) throw new CommandMethodException("quarters.command.quarters.wand.feedback.disabled");

        Player player = getSenderAsPlayerOrThrow(stack);

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();

        boolean canReceivedFreeWand = rmm.canReceiveFreeWand(resident);
        if (!canReceivedFreeWand) throw new CommandMethodException("quarters.command.quarters.wand.feedback.already_received");

        long remainingCooldown = rmm.getRemainingFreeWandCooldown(resident);
        if (remainingCooldown > 0) throw new CommandMethodException(
                "quarters.command.quarters.wand.feedback.cooldown",
                Argument.string("duration", formatDuration(remainingCooldown))
        );

        HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(new ItemStack(ConfigManager.getWandMaterial()));
        if (!remaining.isEmpty()) throw new CommandMethodException("quarters.command.quarters.wand.feedback.inventory_full");

        rmm.incrementReceivedFreeWands(resident);
        rmm.setLastReceivedFreeWand(resident);

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.wand.feedback.success");
    }

    /**
     * Formats seconds into a readable format. Supporting up to years for niche use-cases of servers if they decide to use a yearly cooldown.
     * @param seconds Amount of seconds to parse.
     * @return A formatted string, example: 1w 23d 7h 3m 37s.
     */
    private static @NotNull String formatDuration(long seconds) {
        final long SECONDS_PER_MINUTE = 60;
        final long SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
        final long SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;
        final long SECONDS_PER_WEEK = 7 * SECONDS_PER_DAY;
        final long SECONDS_PER_MONTH = 30 * SECONDS_PER_DAY;
        final long SECONDS_PER_YEAR = 365 * SECONDS_PER_DAY;

        long years = seconds / SECONDS_PER_YEAR;
        seconds %= SECONDS_PER_YEAR;

        long months = seconds / SECONDS_PER_MONTH;
        seconds %= SECONDS_PER_MONTH;

        long weeks = seconds / SECONDS_PER_WEEK;
        seconds %= SECONDS_PER_WEEK;

        long days = seconds / SECONDS_PER_DAY;
        seconds %= SECONDS_PER_DAY;

        long hours = seconds / SECONDS_PER_HOUR;
        seconds %= SECONDS_PER_HOUR;

        long minutes = seconds / SECONDS_PER_MINUTE;
        long secs = seconds % SECONDS_PER_MINUTE;

        StringBuilder result = new StringBuilder();

        if (years > 0) result.append(years).append("y ");
        if (months > 0) result.append(months).append("mo ");
        if (weeks > 0) result.append(weeks).append("w ");
        if (days > 0) result.append(days).append("d ");
        if (hours > 0) result.append(hours).append("h ");
        if (minutes > 0) result.append(minutes).append("m ");
        if (secs > 0 || result.isEmpty()) result.append(secs).append("s");

        return result.toString().trim();
    }
}
