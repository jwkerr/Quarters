package net.earthmc.quarters.api.manager;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.CuboidSelection;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {

    private static ParticleManager instance;

    private ParticleManager() {}

    public static ParticleManager getInstance() {
        if (instance == null) instance = new ParticleManager();
        return instance;
    }

    public void drawCuboidOutline(@NotNull Player player, @Nullable Cuboid cuboid, @NotNull Particle particle, @Nullable Particle.DustOptions dustOptions) {
        if (cuboid == null) return;

        if (!player.getWorld().equals(cuboid.getWorld())) return;

        if (cuboid.distanceTo(player.getLocation()) > Math.pow(ConfigManager.getMaxDistanceForCuboidParticles(), 2)) return;

        for (Location location : computeCuboidEdges(cuboid)) {
            if (dustOptions != null) {
                player.spawnParticle(particle, location, 1, dustOptions);
            } else {
                player.spawnParticle(particle, location, 1, 0, 0, 0, 0);
            }
        }
    }

    public void drawParticlesAtCurrentSelection(Player player) {
        CuboidSelection selection = SelectionManager.getInstance().getSelection(player);
        drawCuboidOutline(player, selection.getCuboid(), ConfigManager.getCurrentSelectionParticle(), null);

        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);
        for (Cuboid cuboid : cuboids) {
            drawCuboidOutline(player, cuboid, ConfigManager.getCurrentCuboidsParticle(), null);
        }
    }

    public void drawParticlesAtQuarters(Player player) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        List<Quarter> quarters = QuarterManager.getInstance().getQuarters(town);
        for (Quarter quarter : quarters) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(
                    Color.fromARGB(quarter.getColour().getRGB()),
                    ResidentMetadataManager.getInstance().getParticleSize(resident)
            );

            for (Cuboid cuboid : quarter.getCuboids()) {
                drawCuboidOutline(player, cuboid, Particle.REDSTONE, dustOptions);
            }
        }
    }

    public List<Location> computeCuboidEdges(@NotNull Cuboid cuboid) {
        Location cornerOne = cuboid.getCornerOne();
        Location cornerTwo = cuboid.getCornerTwo();

        World world = cuboid.getWorld();

        int x1 = cornerOne.getBlockX();
        int y1 = cornerOne.getBlockY();
        int z1 = cornerOne.getBlockZ();
        int x2 = cornerTwo.getBlockX();
        int y2 = cornerTwo.getBlockY();
        int z2 = cornerTwo.getBlockZ();

        List<Location> locations = new ArrayList<>();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            locations.add(getBlockCentredLocation(world, x, y1, z1));
            locations.add(getBlockCentredLocation(world, x, y2, z1));
            locations.add(getBlockCentredLocation(world, x, y1, z2));
            locations.add(getBlockCentredLocation(world, x, y2, z2));
        }

        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            locations.add(getBlockCentredLocation(world, x1, y, z1));
            locations.add(getBlockCentredLocation(world, x2, y, z1));
            locations.add(getBlockCentredLocation(world, x1, y, z2));
            locations.add(getBlockCentredLocation(world, x2, y, z2));
        }

        for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
            locations.add(getBlockCentredLocation(world, x1, y1, z));
            locations.add(getBlockCentredLocation(world, x2, y1, z));
            locations.add(getBlockCentredLocation(world, x1, y2, z));
            locations.add(getBlockCentredLocation(world, x2, y2, z));
        }

        return locations;
    }

    public Location getBlockCentredLocation(World world, int x, int y, int z) {
        return new Location(world, x + 0.5, y + 0.5, z + 0.5);
    }
}
