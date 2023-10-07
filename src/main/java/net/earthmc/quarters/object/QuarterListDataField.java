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

    /**
     * Example input string: world+23+71+70,world+17+62+76,2563fe14-4843-4f16-b28c-e3b7e59fb8f2,02158d9e-2f71-4750-a27b-96493413d09e,fed0ec4a-f1ad-4b97-9443-876391668b34,f17d77ab-aed4-44e7-96ef-ec9cd473eda3+7395d056-536a-4cf3-9c96-6c7a7df6897a|world+23+71+70,world+17+62+76,2563fe14-4843-4f16-b28c-e3b7e59fb8f2,02158d9e-2f71-4750-a27b-96493413d09e,fed0ec4a-f1ad-4b97-9443-876391668b34,f17d77ab-aed4-44e7-96ef-ec9cd473eda3+7395d056-536a-4cf3-9c96-6c7a7df6897a
     * Above string represented by name: pos1,pos2,uuid,town,owner,trustedPlayers
     * The "+" between trustedPlayers delimits a new entry in the list, owner and trustedPlayers can have the values of "null"
     * The "|" delimits different quarter lists, this will represent a whole new entry of type "Quarter" in the final list
     *
     * @param string Input string to set value from
     */
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

            String quarterString = pos1 + "," + pos2 + "," + uuid + "," + town + "," + owner + "," + trustedPlayers + "," + price;

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
