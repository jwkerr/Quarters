package au.lupine.quarters.object.wrapper;

import au.lupine.quarters.object.entity.Cuboid;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class CuboidSelection {

    private Location cornerOne;
    private Location cornerTwo;

    public @Nullable Cuboid getCuboid() {
        if (cornerOne == null || cornerTwo == null) return null;

        try {
            return new Cuboid(cornerOne, cornerTwo);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setCornerOne(Location location) {
        this.cornerOne = location.clone();
    }

    public @Nullable Location getCornerOne() {
        return cornerOne == null ? null : cornerOne.clone();
    }

    public void setCornerTwo(Location location) {
        this.cornerTwo = location.clone();
    }

    public @Nullable Location getCornerTwo() {
        return cornerTwo == null ? null : cornerTwo.clone();
    }
}
