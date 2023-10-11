package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.metadata.DataFieldDeserializer;
import net.earthmc.quarters.util.QuarterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuarterListDFDeserializer implements DataFieldDeserializer<QuarterListDataField> {
    @Override
    public @Nullable QuarterListDataField deserialize(@NotNull String key, @Nullable String value) {
        List<Quarter> quarterList;

        if (value == null) {
            quarterList = new ArrayList<>();
        } else {
            quarterList = QuarterUtil.deserializeQuarterListString(value);
        }

        return new QuarterListDataField(key, quarterList);
    }
}
