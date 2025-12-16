package au.lupine.quarters.api.manager;

import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.CuboidSelection;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ParticleManager {

    private static ParticleManager instance;

    private ParticleManager() {}

    public static ParticleManager getInstance() {
        if (instance == null) instance = new ParticleManager();
        return instance;
    }

    public void drawParticlesAtCurrentSelection(Player player) {
        CuboidSelection selection = SelectionManager.getInstance().getSelection(player);
        drawCuboidOutline(player, selection.getCuboid(), ConfigManager.getCurrentSelectionParticle(), null);

        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);
        for (Cuboid cuboid : cuboids) {
            drawCuboidOutline(player, cuboid, ConfigManager.getCurrentCuboidsParticle(), null);
        }
    }

    public void drawParticlesAtAllQuarters(Player player) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        List<Quarter> quarters = QuarterManager.getInstance().getQuarters(town);
        for (Quarter quarter : quarters) {
            drawParticlesAtQuarter(quarter, resident);
        }
    }

    public void drawParticlesAtQuarter(@NotNull Quarter quarter, @NotNull Resident resident) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(
                Color.fromARGB(quarter.getColour().getRGB()),
                quarter.getParticleSizeOrResidentDefault(resident)
        );

        for (Cuboid cuboid : quarter.getCuboids()) {
            drawCuboidOutline(resident.getPlayer(), cuboid, Particle.REDSTONE, dustOptions);
        }
    }

    public void drawCuboidOutline(@NotNull Player player, @Nullable Cuboid cuboid, @NotNull Particle particle, @Nullable Particle.DustOptions dustOptions) {
        if (cuboid == null) return;

        if (!player.getWorld().equals(cuboid.getWorld())) return;

        for (Location location : computeCuboidEdges(cuboid, player.getLocation())) {
            if (dustOptions != null) {
                player.spawnParticle(particle, location, 1, dustOptions);
            } else {
                player.spawnParticle(particle, location, 1, 0, 0, 0, 0);
            }
        }
    }

    public List<Location> computeCuboidEdges(@NotNull Cuboid cuboid, @NotNull Location viewerLocation) {
        Location cornerOne = cuboid.getCornerOne();
        Location cornerTwo = cuboid.getCornerTwo();

        World world = cuboid.getWorld();

        int x1 = cornerOne.getBlockX();
        int y1 = cornerOne.getBlockY();
        int z1 = cornerOne.getBlockZ();
        int x2 = cornerTwo.getBlockX();
        int y2 = cornerTwo.getBlockY();
        int z2 = cornerTwo.getBlockZ();

        final int range = ConfigManager.getMaxDistanceForCuboidParticles();
        final int viewerX = viewerLocation.getBlockX();
        final int viewerY = viewerLocation.getBlockY();
        final int viewerZ = viewerLocation.getBlockZ();

        final int minX = Math.min(x1, x2);
        final int maxX = Math.max(x1, x2);
        final int minY = Math.min(y1, y2);
        final int maxY = Math.max(y1, y2);
        final int minZ = Math.min(z1, z2);
        final int maxZ = Math.max(z1, z2);

        // skip doing any work if the viewer is more than range blocks outside the cuboid
        if (viewerX <= minX - range || viewerX >= maxX + range || viewerY <= minY - range || viewerY >= maxY + range || viewerZ <= minZ - range || viewerZ >= maxZ + range) {
            return List.of();
        }

        // do the same but for being inside the cuboid
        if (viewerX >= minX + range && viewerX <= maxX - range && viewerZ >= minZ + range && viewerZ <= maxZ - range) {
            return List.of();
        }

        final int rangedMinX = Math.max(minX, viewerX - range);
        final int rangedMaxX = Math.min(maxX, viewerX + range);
        final int rangedMinY = Math.max(minY, viewerY - range);
        final int rangedMaxY = Math.min(maxY, viewerY + range);
        final int rangedMinZ = Math.max(minZ, viewerZ - range);
        final int rangedMaxZ = Math.min(maxZ, viewerZ + range);

        List<Location> locations = new ArrayList<>();
        final TriConsumer<Integer, Integer, Integer> visitor = (x, y, z) -> {
            if (x >= rangedMinX && x <= rangedMaxX && y >= rangedMinY && y <= rangedMaxY && z >= rangedMinZ && z <= rangedMaxZ) {
                locations.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
            }
        };

        for (int x = rangedMinX; x <= rangedMaxX; x++) {
            visitor.accept(x, y1, z1);
            visitor.accept(x, y2, z1);
            visitor.accept(x, y1, z2);
            visitor.accept(x, y2, z2);
        }

        for (int y = rangedMinY; y <= rangedMaxY; y++) {
            visitor.accept(x1, y, z1);
            visitor.accept(x2, y, z1);
            visitor.accept(x1, y, z2);
            visitor.accept(x2, y, z2);
        }

        for (int z = rangedMinZ; z <= rangedMaxZ; z++) {
            visitor.accept(x1, y1, z);
            visitor.accept(x2, y1, z);
            visitor.accept(x1, y2, z);
            visitor.accept(x2, y2, z);
        }

        return locations;
    }

    @FunctionalInterface
    private interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}
