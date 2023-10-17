package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.TownMetadataManager;

import java.util.List;
import java.util.UUID;

public class Quarter {
    private List<Cuboid> cuboids;
    private UUID uuid;
    private Town town;
    private Resident owner;
    private List<Resident> trustedResidents;
    private Double price;
    private QuarterType type;
    private boolean isEmbassy;
    private Long registered;
    private Long claimedAt;

    public void save() {
        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList ==  null)
            return;

        for (int i = 0; i < quarterList.size(); i++) {
            Quarter quarter = quarterList.get(i);
            if (quarter.getUUID().equals(uuid)) {
                quarterList.set(i, this);
                TownMetadataManager.setQuarterListOfTown(town, quarterList);
                break;
            }
        }
    }

    public void delete() {
        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList ==  null)
            return;

        for (int i = 0; i < quarterList.size(); i++) {
            Quarter quarter = quarterList.get(i);
            if (quarter.getUUID().equals(uuid)) {
                quarterList.remove(i);
                TownMetadataManager.setQuarterListOfTown(town, quarterList);
                break;
            }
        }
    }

    public int getVolume() {
        int volume = 0;

        for (Cuboid cuboid : cuboids) {
            volume = volume + (cuboid.getLength() * cuboid.getHeight() * cuboid.getWidth());
        }

        return volume;
    }

    public void setCuboids(List<Cuboid> cuboids) {
        this.cuboids = cuboids;
    }

    public List<Cuboid> getCuboids() {
        return this.cuboids;
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

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setType(QuarterType type) {
        this.type = type;
    }

    public QuarterType getType() {
        return type;
    }

    public void setEmbassy(boolean isEmbassy) {
        this.isEmbassy = isEmbassy;
    }

    public boolean isEmbassy() {
        return isEmbassy;
    }

    public void setRegistered(Long registered) {
        this.registered = registered;
    }

    public Long getRegistered() {
        return registered;
    }

    public void setClaimedAt(Long claimedAt) {
        this.claimedAt = claimedAt;
    }

    public Long getClaimedAt() {
        return claimedAt;
    }
}
