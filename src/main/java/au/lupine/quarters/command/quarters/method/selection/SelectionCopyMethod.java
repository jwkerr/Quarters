package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.exception.CommandMethodException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SelectionCopyMethod extends CommandMethod {

    public static final Map<UUID, Clipboard> CLIPBOARD_MAP = new ConcurrentHashMap<>();

    public SelectionCopyMethod() {
        super("copy", "quarters.command.quarters.selection.copy");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);
        if (cuboids.isEmpty()) throw new CommandMethodException("quarters.command.quarters.selection.feedback.none");

        CLIPBOARD_MAP.put(player.getUniqueId(), Clipboard.copyOf(cuboids, getBlockLocation(player)));

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.selection.copy.feedback.success");
    }

    private static @NotNull Location getBlockLocation(@NotNull Player player) {
        Location location = player.getLocation();
        return new Location(player.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * A world-independent clipboard whose cuboids are stored as offsets from the player's block.
     */
    public record Clipboard(@NotNull List<RelativeCuboid> cuboids) {

        public Clipboard {
            cuboids = List.copyOf(cuboids);
        }

        public static @NotNull Clipboard copyOf(@NotNull List<Cuboid> cuboids, @NotNull Location anchor) {
            return new Clipboard(cuboids.stream()
                    .map(cuboid -> RelativeCuboid.from(cuboid, anchor))
                    .toList());
        }

        public @NotNull List<Cuboid> pasteAt(@NotNull Location anchor) {
            World world = anchor.getWorld();
            if (world == null) throw new IllegalArgumentException("Clipboard anchor must have a world");

            return cuboids.stream()
                    .map(cuboid -> cuboid.pasteAt(world, anchor.getBlockX(), anchor.getBlockY(), anchor.getBlockZ()))
                    .toList();
        }
    }

    /**
     * Integer block offsets for one cuboid in a {@link Clipboard}.
     */
    public record RelativeCuboid(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

        private static @NotNull RelativeCuboid from(@NotNull Cuboid cuboid, @NotNull Location anchor) {
            return new RelativeCuboid(
                    cuboid.getMinX() - anchor.getBlockX(),
                    cuboid.getMinY() - anchor.getBlockY(),
                    cuboid.getMinZ() - anchor.getBlockZ(),
                    cuboid.getMaxX() - anchor.getBlockX(),
                    cuboid.getMaxY() - anchor.getBlockY(),
                    cuboid.getMaxZ() - anchor.getBlockZ()
            );
        }

        private @NotNull Cuboid pasteAt(@NotNull World world, int anchorX, int anchorY, int anchorZ) {
            return new Cuboid(
                    new Location(world, anchorX + minX, anchorY + minY, anchorZ + minZ),
                    new Location(world, anchorX + maxX, anchorY + maxY, anchorZ + maxZ)
            );
        }
    }
}
