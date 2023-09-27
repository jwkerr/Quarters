package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Quarter {
    Location pos1;
    Location pos2;
    UUID uuid;
    Town town;
    Player owner;
    List<Player> trustedPlayers;

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

    public void setOwner(UUID uuid) {
        if (uuid != null) {
            this.owner = Bukkit.getPlayer(uuid);
        }
    }

    public Player getOwner() {
        return owner;
    }

    public void setTrustedPlayers(List<Player> playerList) {
        this.trustedPlayers = playerList;
    }

    public List<Player> getTrustedPlayers() {
        return trustedPlayers;
    }
}
