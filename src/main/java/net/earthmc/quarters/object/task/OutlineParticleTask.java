package net.earthmc.quarters.object.task;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.api.manager.SelectionManager;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.wrapper.CuboidSelection;
import net.earthmc.quarters.object.entity.Quarter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OutlineParticleTask {

    public static void startTask(final Player player, final Quarters plugin) {
        player.getScheduler().runAtFixedRate(plugin, task -> {
            if (!QuarterManager.getInstance().shouldRenderOutlines(player)) return;

            createParticlesIfSelectionExists(player);

            createParticlesIfQuartersExist(player);
        }, () -> {}, 1L, ConfigManager.getTicksBetweenParticleOutlines());
    }

    public static void createParticlesIfSelectionExists(Player player) {
        CuboidSelection selection = SelectionManager.getInstance().getSelection(player);
        createParticlesAtCuboidEdges(player, selection.getCuboid(), ConfigManager.getCurrentSelectionParticle(), null);

        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);
        for (Cuboid cuboid : cuboids) {
            createParticlesAtCuboidEdges(player, cuboid, ConfigManager.getCurrentCuboidsParticle(), null);
        }
    }

    public static void createParticlesIfQuartersExist(Player player) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return;

        List<Quarter> quarters = QuarterManager.getInstance().getQuarters(town);
        for (Quarter quarter : quarters) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromARGB(quarter.getColour().getRGB()), 1f);

            for (Cuboid cuboid : quarter.getCuboids()) {
                createParticlesAtCuboidEdges(player, cuboid, Particle.REDSTONE, dustOptions);
            }
        }
    }

    private static void createParticlesAtCuboidEdges(Player player, Cuboid cuboid, Particle particle, Particle.DustOptions dustOptions) {
        if (cuboid == null) return;

        World playerWorld = player.getWorld();
        if (!playerWorld.equals(cuboid.getWorld())) return;

        int maxDistance = ConfigManager.getMaxDistanceForCuboidParticles(); // Skip sending particles if the cuboid is too far to save performance
        int maxDistanceSquared = maxDistance * maxDistance;
        if (cuboid.distanceTo(player.getLocation()) > maxDistanceSquared) return;

        Location cornerOne = cuboid.getCornerOne();
        Location cornerTwo = cuboid.getCornerTwo();

        int x1 = cornerOne.getBlockX();
        int y1 = cornerOne.getBlockY();
        int z1 = cornerOne.getBlockZ();
        int x2 = cornerTwo.getBlockX();
        int y2 = cornerTwo.getBlockY();
        int z2 = cornerTwo.getBlockZ();

        List<int[]> edges = new ArrayList<>();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            edges.add(new int[]{x, y1, z1});
            edges.add(new int[]{x, y2, z1});
            edges.add(new int[]{x, y1, z2});
            edges.add(new int[]{x, y2, z2});
        }

        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            edges.add(new int[]{x1, y, z1});
            edges.add(new int[]{x2, y, z1});
            edges.add(new int[]{x1, y, z2});
            edges.add(new int[]{x2, y, z2});
        }

        for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
            edges.add(new int[]{x1, y1, z});
            edges.add(new int[]{x2, y1, z});
            edges.add(new int[]{x1, y2, z});
            edges.add(new int[]{x2, y2, z});
        }

        for (int[] coordinate : edges) {
            Location location = new Location(player.getWorld(), coordinate[0] + 0.5, coordinate[1] + 0.5, coordinate[2] + 0.5);
            if (dustOptions == null) {
                player.spawnParticle(particle, location, 1, 0, 0, 0, 0);
            } else {
                player.spawnParticle(particle, location, 1, dustOptions);
            }
        }
    }
}
