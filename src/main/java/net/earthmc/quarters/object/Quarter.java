package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.TownMetadataManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quarter {
    private List<Cuboid> cuboids;
    private UUID uuid;
    private Town town;
    private UUID owner;
    private List<UUID> trusted;
    private Double price;
    private QuarterType type;
    private boolean isEmbassy;
    private Long registered;
    private Long claimedAt;
    private int[] rgb;

    /**
     * This method must be called to save the quarter's instance to metadata after any change
     * The only exception is when using the delete() method, that will save itself
     */
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

    /**
     * Permanently deletes the quarter from the town's metadata
     */
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

    /**
     *
     * @return An int representing the total number of blocks inside the quarter
     */
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

    /**
     *
     * @return A list of the cuboids within the quarter
     */
    public List<Cuboid> getCuboids() {
        return this.cuboids;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     *
     * @return A unique ID for the quarter, this will not change across restarts and is there for internal use when saving quarters
     */
    public UUID getUUID() {
        return uuid;
    }

    public void setTown(UUID uuid) {
        this.town = TownyAPI.getInstance().getTown(uuid);
    }

    /**
     *
     * @return The town that the quarter is located in
     */
    public Town getTown() {
        return town;
    }

    public QuartersTown getQuartersTown() {
        return new QuartersTown(town);
    }

    /**
     *
     * @return Gets the quarter owner's resident instance, if you want to change the owner use the setOwner() method that takes in a UUID
     */
    public Resident getOwnerResident() {
        UUID ownerUUID = getOwner();
        if (ownerUUID == null)
            return null;

        return TownyAPI.getInstance().getResident(ownerUUID);
    }

    /**
     *
     * @return Gets a list of all the trusted residents in the quarter, if you want to change this use the setTrusted() method that takes a list of UUIDs
     */
    public List<Resident> getTrustedResidents() {
        List<Resident> trustedResidents = new ArrayList<>();

        for (UUID trustedUUID : getTrusted()) {
            trustedResidents.add(TownyAPI.getInstance().getResident(trustedUUID));
        }

        return trustedResidents;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     *
     * @return The current sale price or null if it is not for sale
     */
    @Nullable
    public Double getPrice() {
        return price;
    }

    public void setType(QuarterType type) {
        this.type = type;
    }

    /**
     *
     * @return The quarter's {@link QuarterType}
     */
    public QuarterType getType() {
        return type;
    }

    /**
     * When changing a quarter's owner, care should always be taken to ensure that the new owner is in fact part of the town if the quarter is not an embassy
     *
     * @param isEmbassy A boolean representing the state you want the embassy status to be in
     */
    public void setEmbassy(boolean isEmbassy) {
        this.isEmbassy = isEmbassy;
    }

    /**
     * Gets the quarter's embassy status
     * As opposed to how Towny handles embassies, embassy is not its own quarter type, it is a separate flag
     * Being an embassy allows for certain conditions outside just ownership when not in the quarter's town
     * for example, quarters of the station or common type will have their functionality extended to non-residents
     *
     * @return A boolean representing whether the quarter is an embassy
     */
    public boolean isEmbassy() {
        return isEmbassy;
    }

    public void setRegistered(Long registered) {
        this.registered = registered;
    }

    /**
     *
     * @return A Long representing when the quarter was created
     */
    public Long getRegistered() {
        return registered;
    }

    public void setClaimedAt(Long claimedAt) {
        this.claimedAt = claimedAt;
    }

    /**
     *
     * @return A Long representing when ownership of a quarter last changed
     */
    public Long getClaimedAt() {
        return claimedAt;
    }

    public void setRGB(int[] rgb) {
        for (int i = 0; i < rgb.length; i++) {
            if (rgb[i] < 0) {
                rgb[i] = 0;
            } else if (rgb[i] > 255) {
                rgb[i] = 255;
            }
        }

        this.rgb = rgb;
    }

    /**
     *
     * @return An int array representing an RGB value for the quarter's outline
     */
    public int[] getRGB() {
        return rgb;
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    /**
     *
     * @return The quarter owner's UUID
     */
    public UUID getOwner() {
        return owner;
    }

    public void setTrusted(List<UUID> uuidList) {
        this.trusted = uuidList;
    }

    /**
     *
     * @return A list of trusted residents' UUIDs
     */
    public List<UUID> getTrusted() {
        return trusted;
    }
}
