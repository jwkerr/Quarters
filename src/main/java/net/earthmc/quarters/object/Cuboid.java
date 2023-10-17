package net.earthmc.quarters.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cuboid {
    private final Location pos1;
    private final Location pos2;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int length;
    private final int height;
    private final int width;

    public Cuboid(Location pos1, Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        this.length = Math.abs(pos1.getBlockX() - pos2.getBlockZ());
        this.height = Math.abs(pos1.getBlockY() - pos2.getBlockY());
        this.width = Math.abs(pos1.getBlockZ() - pos2.getBlockZ());
    }

    public boolean doesIntersectWith(Cuboid cuboid) {
        if (pos1.getWorld() != cuboid.getPos1().getWorld())
            return false;

        boolean overlapX = (maxX >= cuboid.getMinX()) && (cuboid.getMaxX() >= minX);
        boolean overlapY = (maxY >= cuboid.getMinY()) && (cuboid.getMaxY() >= minY);
        boolean overlapZ = (maxZ >= cuboid.getMinZ()) && (cuboid.getMaxZ() >= minZ);

        return overlapX && overlapY && overlapZ;
    }

    public boolean isLocationInsideBounds(Location location) {
        if (location.getY() < minY || location.getY() > maxY + 0.99999)
            return false;

        if (location.getX() < minX || location.getX() > maxX + 0.99999)
            return false;

        if (location.getZ() < minZ || location.getZ() > maxZ + 0.99999)
            return false;

        return true;
    }

    public List<Player> getPlayersInCuboid() {
        List<Player> playersInCuboid = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLocationInsideBounds(player.getLocation()))
                playersInCuboid.add(player);
        }

        return playersInCuboid;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
