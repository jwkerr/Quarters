package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.earthmc.quarters.util.QuarterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuarterListDataField extends CustomDataField<List<Quarter>> {
    public QuarterListDataField(String key, List<Quarter> value, String label) {
        super(key, value, label);
    }

    public QuarterListDataField(String key, List<Quarter> value) {
        super(key, value);
    }

    @NotNull
    public static String typeID() {
        return "quarters_quarterlistdf";
    }

    @Override
    public @NotNull String getTypeID() {
        return typeID();
    }

    @Override
    public void setValueFromString(String string) {
        this.setValue(QuarterUtil.deserializeQuarterListString(string));
    }

    @Override
    protected String displayFormattedValue() {
       return String.valueOf(this.getValue().size());
    }

    @Override
    protected @Nullable String serializeValueToString() {
        final List<Quarter> quarterList = this.getValue();

        if (quarterList == null || quarterList.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();
        for (Quarter quarter : quarterList) {
            if (sb.length() > 0)
                sb.append("|");

            String cuboids = QuarterUtil.serializeCuboids(quarter.getCuboids());
            String uuid = quarter.getUUID().toString();
            String town = quarter.getTown().getUUID().toString();

            String owner;
            if (quarter.getOwnerUUID() != null) {
                owner = quarter.getOwnerUUID().toString();
            } else {
                owner = "null";
            }

            String trustedPlayers = QuarterUtil.serializeUUIDList(quarter.getTrustedResidentsUUIDs());

            String price;
            if (quarter.getPrice() != null) {
                price = quarter.getPrice().toString();
            } else {
                price = "null";
            }

            String type = quarter.getType().getName();

            String embassy;
            if (quarter.isEmbassy()) {
                embassy = "true";
            } else {
                embassy = "false";
            }

            String registered = quarter.getRegistered().toString();

            String claimedAt;
            if (quarter.getClaimedAt() == null) {
                claimedAt = "null";
            } else {
                claimedAt = quarter.getClaimedAt().toString();
            }

            int[] rgbArray = quarter.getRGB();
            String rgb = rgbArray[0] + "+" + rgbArray[1] + "+" + rgbArray[2];

            String quarterString = cuboids + "," + uuid + "," + town + "," + owner + "," + trustedPlayers + "," + price + "," + type + "," + embassy + "," + registered + "," + claimedAt + "," + rgb;

            sb.append(quarterString);
        }

        return sb.toString();
    }

    @Override
    public @NotNull CustomDataField<List<Quarter>> clone() {
        final List<Quarter> quarterList = this.getValue();
        List<Quarter> copyList = null;

        if (quarterList != null)
            copyList = new ArrayList<>(quarterList);

        final String copyLabel = hasLabel() ? getLabel() : null;

        return new QuarterListDataField(this.getKey(), copyList, copyLabel);
    }
}
