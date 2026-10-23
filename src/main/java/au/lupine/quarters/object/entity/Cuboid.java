package au.lupine.quarters.object.entity;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.state.CuboidValidity;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        World cornerOneWorld = Objects.requireNonNull(cornerOne.getWorld(), "Cuboid corner one must have a world");
        World cornerTwoWorld = Objects.requireNonNull(cornerTwo.getWorld(), "Cuboid corner two must have a world");
        if (!cornerOneWorld.equals(cornerTwoWorld)) throw new IllegalArgumentException("Cuboid corners must be within the same world");

        this.cornerOne = new Location(cornerOneWorld, cornerOne.getBlockX(), cornerOne.getBlockY(), cornerOne.getBlockZ());
        this.cornerTwo = new Location(cornerTwoWorld, cornerTwo.getBlockX(), cornerTwo.getBlockY(), cornerTwo.getBlockZ());
        this.world = cornerOneWorld;
        this.cornerBlockOne = world.getBlockAt(this.cornerOne);
        this.cornerBlockTwo = world.getBlockAt(this.cornerTwo);
        this.bounding = BoundingBox.of(cornerBlockOne, cornerBlockTwo);
        this.minX = Math.min(this.cornerOne.getBlockX(), this.cornerTwo.getBlockX());
        this.maxX = Math.max(this.cornerOne.getBlockX(), this.cornerTwo.getBlockX());
        this.minY = Math.min(this.cornerOne.getBlockY(), this.cornerTwo.getBlockY());
        this.maxY = Math.max(this.cornerOne.getBlockY(), this.cornerTwo.getBlockY());
        this.minZ = Math.min(this.cornerOne.getBlockZ(), this.cornerTwo.getBlockZ());
        this.maxZ = Math.max(this.cornerOne.getBlockZ(), this.cornerTwo.getBlockZ());
        this.length = Math.abs(this.cornerOne.getBlockX() - this.cornerTwo.getBlockX()) + 1;
        this.height = Math.abs(this.cornerOne.getBlockY() - this.cornerTwo.getBlockY()) + 1;
        this.width = Math.abs(this.cornerOne.getBlockZ() - this.cornerTwo.getBlockZ()) + 1;
        this.volume = length * height * width;
    }

    /**
     * Runs a few checks to ensure the cuboid isn't in a "wrong" location such as wilderness or spanning across multiple towns
     *
     * @return True if the cuboid instance is in a valid location to be part of a quarter
     */
    /**
     * Gets every validation problem with this cuboid.
     *
     * @return an immutable list of all issues, or an empty list when this cuboid is valid
     */
    public @NotNull List<CuboidValidity> checkValidity() {
        List<CuboidValidity> issues = new ArrayList<>();

        if (!isCuboidInsideWorldBounds()) issues.add(CuboidValidity.OUTSIDE_WORLD_BOUNDS);

        boolean doesNotContainWilderness = doesCuboidNotContainWilderness();
        if (!doesNotContainWilderness) issues.add(CuboidValidity.CONTAINS_WILDERNESS);

        if (doesCuboidIntersectWithPreExistingQuarters()) issues.add(CuboidValidity.INTERSECTS);

        if (doesNotContainWilderness && !isCuboidEntirelyWithinSingularTown()) {
            issues.add(CuboidValidity.SPANS_MULTIPLE_TOWNS);
        }

        int maxCuboidVolume = Quarters.getInstance().config().quarters.maxCuboidVolume;
        if (maxCuboidVolume > -1 && this.getVolume() > maxCuboidVolume) issues.add(CuboidValidity.TOO_LARGE);

        return List.copyOf(issues);
    }

    public boolean isCuboidInsideWorldBounds() {
        SelectionManager sm = SelectionManager.getInstance();
        return sm.isLocationInsideWorldBounds(cornerOne) && sm.isLocationInsideWorldBounds(cornerTwo);
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
     * Iterates over every x and z coordinate within the cuboid but not y and tests the provided predicate, useful for things that don't have verticality such as townblocks
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

        return intersectsWith(cuboid.getBounding());
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
    public List<Player> getPlayersInsideBounds() {
        List<Player> playersInCuboid = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLocationInsideBounds(player.getLocation())) playersInCuboid.add(player);
        }

        return playersInCuboid;
    }

    /**
     * @return The exact centre between both corners
     */
    public @NotNull Location getMidPoint() {
        double x = (cornerOne.getX() + cornerTwo.getX()) / 2;
        double y = (cornerOne.getY() + cornerTwo.getY()) / 2;
        double z = (cornerOne.getZ() + cornerTwo.getZ()) / 2;

        return new Location(world, x, y, z);
    }

    /**
     * @return The distance from the midpoint of this cuboid to the specified location
     */
    public double distanceTo(@NotNull Location location) {
        return getMidPoint().distance(location);
    }

    /**
     * @return A new cuboid with the specified location subtracted from its corners
     */
    public Cuboid subtract(@NotNull Location location) {
        Location subOne = cornerOne.clone().subtract(location);
        Location subTwo = cornerTwo.clone().subtract(location);

        return new Cuboid(subOne, subTwo);
    }

    /**
     * @return A new cuboid with the specified location added to its corners
     */
    public Cuboid add(@NotNull Location location) {
        Location addOne = cornerOne.clone().add(location);
        Location addTwo = cornerTwo.clone().add(location);

        return new Cuboid(addOne, addTwo);
    }

    public Location getCornerOne() {
        return cornerOne.clone();
    }

    public Location getCornerTwo() {
        return cornerTwo.clone();
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
