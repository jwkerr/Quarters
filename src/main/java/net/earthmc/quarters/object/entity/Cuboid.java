package net.earthmc.quarters.object.entity;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.state.CuboidValidity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents an immutable, arbitrarily sized and shaped cuboid within a world
 */
public class Cuboid {

    private final Location cornerOne, cornerTwo;
    private final World world;
    private final Block cornerBlockOne, cornerBlockTwo;
    private final BoundingBox bounding;
    private final int minX, minY, minZ, maxX, maxY, maxZ, length, height, width, volume;

    /**
     * This constructor can take arbitrary positions within a block but bounding checks will be measured including the entirety of the corner's block
     * @param cornerOne Cuboid corner one
     * @param cornerTwo Cuboid corner two
     */
    public Cuboid(@NotNull Location cornerOne, @NotNull Location cornerTwo) {
        if (!cornerOne.getWorld().equals(cornerTwo.getWorld())) throw new IllegalArgumentException("Cuboid corners must be within the same world");

        this.cornerOne = cornerOne;
        this.cornerTwo = cornerTwo;
        this.world = cornerOne.getWorld();
        this.cornerBlockOne = world.getBlockAt(cornerOne);
        this.cornerBlockTwo = world.getBlockAt(cornerTwo);
        this.bounding = BoundingBox.of(cornerBlockOne, cornerBlockTwo);
        this.minX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
        this.maxX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
        this.minY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
        this.maxY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
        this.minZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        this.maxZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        this.length = Math.abs(cornerOne.getBlockX() - cornerTwo.getBlockX()) + 1;
        this.height = Math.abs(cornerOne.getBlockY() - cornerTwo.getBlockY()) + 1;
        this.width = Math.abs(cornerOne.getBlockZ() - cornerTwo.getBlockZ()) + 1;
        this.volume = length * height * width;
    }

    /**
     * Runs a few checks to ensure the cuboid isn't in a "wrong" location such as wilderness or spanning across multiple towns
     *
     * @return True if the cuboid instance is in a valid location to be part of a quarter
     */
    public CuboidValidity checkValidity() {
        boolean doesNotContainWilderness = doesCuboidNotContainWilderness();
        if (!doesNotContainWilderness) return CuboidValidity.CONTAINS_WILDERNESS;

        boolean doesIntersect = doesCuboidIntersectWithPreExistingQuarters();
        if (doesIntersect) return CuboidValidity.INTERSECTS;

        if (!isCuboidEntirelyWithinSingularTown()) return CuboidValidity.SPANS_MULTIPLE_TOWNS;

        int maxCuboidVolume = ConfigManager.getMaxCuboidVolume();
        if (maxCuboidVolume > -1 && this.getVolume() > maxCuboidVolume) return CuboidValidity.TOO_LARGE;

        return CuboidValidity.VALID;
    }

    private boolean doesCuboidIntersectWithPreExistingQuarters() {
        Town town = TownyAPI.getInstance().getTown(cornerOne);
        if (town == null) return false;

        List<Quarter> quarters = QuarterManager.getInstance().getQuarters(town);

        for (Quarter quarter : quarters) {
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (intersectsWith(cuboid)) return true;
            }
        }

        return false;
    }

    public boolean doesCuboidNotContainWilderness() {
        return iterateXZ(location -> {
            Town town = TownyAPI.getInstance().getTown(location);
            return town != null;
        });
    }

    public boolean isCuboidEntirelyWithinSingularTown() {
        Town town = TownyAPI.getInstance().getTown(cornerOne);
        if (town == null) return false;

        return iterateXZ(location -> {
            Town currentPosTown = TownyAPI.getInstance().getTown(location);
            if (currentPosTown == null) return false;

            return town.equals(currentPosTown);
        });
    }

    /**
     * Iterates over every block within the cuboid testing the provided predicate, returns true if all blocks pass the predicate
     */
    public boolean iterateAllBlocks(Predicate<Location> predicate) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!predicate.test(new Location(world, x, y, z))) return false;
                }
            }
        }

        return true;
    }

    /**
     * Iterates over every x and z coordinate within the cuboid but not y, useful for things that don't have verticality such as townblocks
     */
    public boolean iterateXZ(Predicate<Location> predicate) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!predicate.test(new Location(world, x, 64, z))) return false;
            }
        }

        return true;
    }

    /**
     * @param cuboid Cuboid to check this cuboid instance against
     * @return True if the specified cuboid has any intersection with this cuboid instance
     */
    public boolean intersectsWith(Cuboid cuboid) {
        if (!world.equals(cuboid.getWorld())) return false;

        return bounding.overlaps(cuboid.getBounding());
    }

    public boolean intersectsWith(BoundingBox bounding) {
        return this.bounding.overlaps(bounding);
    }

    /**
     * @param location Location to check
     * @return True if the specified location is within this cuboid
     */
    public boolean isLocationInsideBounds(Location location) {
        if (!cornerOne.getWorld().equals(location.getWorld())) return false;
        return bounding.contains(location.toVector());
    }

    /**
     * @return A list of all players currently in this cuboid
     */
    public List<Player> getPlayersInBounds() {
        List<Player> playersInCuboid = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLocationInsideBounds(player.getLocation())) playersInCuboid.add(player);
        }

        return playersInCuboid;
    }

    public @NotNull Location getMidPoint() {
        double x = (cornerOne.getX() + cornerTwo.getX()) / 2;
        double y = (cornerOne.getY() + cornerTwo.getY()) / 2;
        double z = (cornerOne.getZ() + cornerTwo.getZ()) / 2;

        return new Location(world, x, y, z);
    }

    public double distanceTo(@NotNull Location location) {
        return getMidPoint().distance(location);
    }

    /**
     * @return A new cuboid with the specified location subtracted from its corner's
     */
    public Cuboid subtract(@NotNull Location location) {
        Location subOne = cornerOne.clone().subtract(location);
        Location subTwo = cornerTwo.clone().subtract(location);

        return new Cuboid(subOne, subTwo);
    }

    public Cuboid add(@NotNull Location location) {
        Location addOne = cornerOne.clone().add(location);
        Location addTwo = cornerTwo.clone().add(location);

        return new Cuboid(addOne, addTwo);
    }

    public Location getCornerOne() {
        return cornerOne;
    }

    public Location getCornerTwo() {
        return cornerTwo;
    }

    public World getWorld() {
        return world;
    }

    public Block getCornerBlockOne() {
        return cornerBlockOne;
    }

    public Block getCornerBlockTwo() {
        return cornerBlockTwo;
    }

    public BoundingBox getBounding() {
        return bounding;
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

    public int getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "cornerone=" + cornerOne.toString().replace("Location", "") +
                ",cornertwo=" + cornerTwo.toString().replace("Location", "");
    }
}
