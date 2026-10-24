package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.entity.Quarter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PvpGracePeriodManager {

    private static PvpGracePeriodManager instance;

    private final Map<UUID, GracePeriod> gracePeriods = new ConcurrentHashMap<>();

    private PvpGracePeriodManager() {}

    public static PvpGracePeriodManager getInstance() {
        if (instance == null) instance = new PvpGracePeriodManager();
        return instance;
    }

    public void start(@NotNull Player player, @NotNull Quarter quarter) {
        ConfigManager.PvpSettings settings = Quarters.getInstance().config().quarters.pvpSettings;
        long seconds = Math.max(settings.entryGracePeriodSeconds, 0);
        if (seconds == 0) {
            clear(player);
            return;
        }

        GracePeriod gracePeriod = new GracePeriod(quarter.getUUID(), System.currentTimeMillis() + seconds * 1000);
        gracePeriods.put(player.getUniqueId(), gracePeriod);

        if (settings.showEntryGracePeriodEnterMessage) {
            QuartersMessaging.sendInfoMessage(player, "quarters.pvp.grace_period.enter", null);
        }

        long delayTicks = seconds * 20;
        player.getScheduler().runDelayed(
                Quarters.getInstance(),
                task -> complete(player, quarter.getUUID(), gracePeriod.expiresAt()),
                () -> {},
                delayTicks
        );
    }

    public void clear(@NotNull Player player) {
        gracePeriods.remove(player.getUniqueId());
    }

    public boolean isProtected(@NotNull Player player, @NotNull Quarter quarter) {
        GracePeriod gracePeriod = gracePeriods.get(player.getUniqueId());
        if (gracePeriod == null) return false;
        if (!gracePeriod.quarterUuid().equals(quarter.getUUID())) return false;

        if (System.currentTimeMillis() > gracePeriod.expiresAt()) {
            gracePeriods.remove(player.getUniqueId(), gracePeriod);
            return false;
        }

        return true;
    }

    private void complete(@NotNull Player player, @NotNull UUID quarterUuid, long expiresAt) {
        GracePeriod gracePeriod = gracePeriods.get(player.getUniqueId());
        if (gracePeriod == null) return;
        if (!gracePeriod.quarterUuid().equals(quarterUuid) || gracePeriod.expiresAt() != expiresAt) return;

        gracePeriods.remove(player.getUniqueId(), gracePeriod);
        if (Quarters.getInstance().config().quarters.pvpSettings.showEntryGracePeriodCompleteMessage) {
            QuartersMessaging.sendInfoMessage(player, "quarters.pvp.grace_period.complete", null);
        }
    }

    private record GracePeriod(@NotNull UUID quarterUuid, long expiresAt) {}
}
