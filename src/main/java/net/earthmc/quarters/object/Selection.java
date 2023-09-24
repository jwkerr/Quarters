package net.earthmc.quarters.object;

import org.bukkit.Location;

import javax.annotation.Nullable;

public class Selection {
    Location pos1 = null;
    Location pos2 = null;

    public void setPos1(Location location) {
        pos1 = location;
    }

    public void setPos2(Location location) {
        pos2 = location;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }
}
