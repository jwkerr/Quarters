package net.earthmc.quarters.object;

import org.bukkit.Location;

public class Selection {
    Location pos1;
    Location pos2;

    public void setPos1(Location location) {
        this.pos1 = location;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos2(Location location) {
        this.pos2 = location;
    }

    public Location getPos2() {
        return pos2;
    }
}
