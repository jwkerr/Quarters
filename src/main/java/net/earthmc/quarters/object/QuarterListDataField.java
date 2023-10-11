package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.earthmc.quarters.utils.QuarterUtils;
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
        this.setValue(QuarterUtils.deserializeQuarterListString(string));
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

            String pos1 = QuarterUtils.serializeLocation(quarter.getPos1());
            String pos2 = QuarterUtils.serializeLocation(quarter.getPos2());
            String uuid = quarter.getUUID().toString();
            String town = quarter.getTown().getUUID().toString();

            String owner;
            if (quarter.getOwner() != null) {
                owner = quarter.getOwner().getUUID().toString();
            } else {
                owner = "null";
            }

            String trustedPlayers = QuarterUtils.serializeResidentList(quarter.getTrustedResidents());

            double price = quarter.getPrice();

            String type = quarter.getType().getName();

            String quarterString = pos1 + "," + pos2 + "," + uuid + "," + town + "," + owner + "," + trustedPlayers + "," + price + "," + type;

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
