package net.earthmc.quarters.task;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.*;
import net.earthmc.quarters.manager.SelectionManager;
import net.earthmc.quarters.util.QuarterUtil;
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
            QuartersPlayer quartersPlayer = new QuartersPlayer(player);
            if (!QuarterUtil.shouldRenderOutlines(quartersPlayer, player.getInventory().getItemInMainHand().getType()))
                return;

            createParticlesIfSelectionExists(player);

            createParticlesIfQuartersExist(player);
        }, () -> {}, 1L, plugin.getConfig().getInt("particles.ticks_between_outline"));
    }

    public static void createParticlesIfSelectionExists(Player player) {
        Selection selection = SelectionManager.selectionMap.get(player);
        List<Cuboid> cuboids = SelectionManager.cuboidsMap.get(player);

        if (selection != null) {
            Location pos1 = selection.getPos1();
            Location pos2 = selection.getPos2();

            if (pos1 != null && pos2 != null) {
                createParticlesAtCuboidEdges(player, pos1, pos2, Particle.valueOf(Quarters.INSTANCE.getConfig().getString("particles.current_selection_particle")), null);
            }
        }

        if (cuboids != null) {
            for (Cuboid cuboid : cuboids) {
                createParticlesAtCuboidEdges(player, cuboid.getPos1(), cuboid.getPos2(), Particle.valueOf(Quarters.INSTANCE.getConfig().getString("particles.current_cuboids_particle")), null);
            }
        }
    }

    public static void createParticlesIfQuartersExist(Player player) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null)
            return;

        QuartersTown quartersTown = new QuartersTown(town);
        List<Quarter> quarterList = quartersTown.getQuarters();
        if (quarterList != null) {
            for (Quarter quarter : quarterList) {
                int[] rgb = quarter.getRGB();
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(rgb[0], rgb[1], rgb[2]), 1f);

                for (Cuboid cuboid : quarter.getCuboids()) {
                    createParticlesAtCuboidEdges(player, cuboid.getPos1(), cuboid.getPos2(), Particle.REDSTONE, dustOptions);
                }
            }
        }
    }

    private static void createParticlesAtCuboidEdges(Player player, Location pos1, Location pos2, Particle particle, Particle.DustOptions dustOptions) {
        int maxDistance = Quarters.INSTANCE.getConfig().getInt("particles.max_distance_from_cuboid"); // Skip sending particles if the cuboid is too far to save performance
        final World playerWorld = player.getWorld();
        if (!playerWorld.equals(pos1.getWorld()) || !playerWorld.equals(pos2.getWorld()))
            return;

        Location playerLocation = player.getLocation();
        final int maxDistanceSquared = maxDistance * maxDistance;
        if (playerLocation.distanceSquared(pos1) > maxDistanceSquared && playerLocation.distanceSquared(pos2) > maxDistanceSquared)
            return;

        int x1 = pos1.getBlockX();
        int y1 = pos1.getBlockY();
        int z1 = pos1.getBlockZ();
        int x2 = pos2.getBlockX();
        int y2 = pos2.getBlockY();
        int z2 = pos2.getBlockZ();

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
