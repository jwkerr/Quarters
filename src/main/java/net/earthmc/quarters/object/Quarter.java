package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class Quarter {
    Location pos1;
    Location pos2;
    UUID uuid;
    Town town;
    Resident owner;
    List<Resident> trustedResidents;
    double price;

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

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setTown(UUID uuid) {
        this.town = TownyAPI.getInstance().getTown(uuid);
    }

    public Town getTown() {
        return town;
    }

    public void setOwner(Resident resident) {
        if (resident != null)
            this.owner = resident;
    }

    public Resident getOwner() {
        return owner;
    }

    public void setTrustedResidents(List<Resident> residentList) {
        this.trustedResidents = residentList;
    }

    public List<Resident> getTrustedResidents() {
        return trustedResidents;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
