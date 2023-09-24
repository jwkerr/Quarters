package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Quarter {
    Location pos1;
    Location pos2;
    Town town;
    Player owner;

    public Town getTown() {
        return town;
    }

    public Player getOwner() {
        return owner;
    }
}
