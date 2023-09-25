package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Quarter {
    UUID uuid;
    Location pos1;
    Location pos2;
    TownBlock townBlock;
    Player owner;
    List<Player> trustedPlayers;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

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

    public void setTownBlock(TownBlock townBlock) {
        this.townBlock = townBlock;
    }

    public TownBlock getTownBlock() {
        return townBlock;
    }

    public void setOwner(Player player) {
        this.owner = player;
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
