package au.lupine.quarters.command.quartersadmin.method.meta;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class AdminMetaSetMethod extends CommandMethod {

    public AdminMetaSetMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.meta.set");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        String key = getArgOrThrow(0, "No meta key provided", false);
        String value = getArgOrThrow(1, "No meta value provided", false);

        CustomDataField<?> cdf = quarter.getMetadata(key);
        if (cdf == null) {
            Map<String, CustomDataField<?>> rmd = TownyUniverse.getInstance().getRegisteredMetadataMap();

            CustomDataField<?> registeredDataField = rmd.get(key);
            if (registeredDataField == null) throw new CommandMethodException("Specified meta key " + key + " does not exist");

            cdf = registeredDataField.clone();
        }

        cdf.setValueFromString(value);

        quarter.addMetaData(cdf, true);

        QuartersMessaging.sendSuccessMessage(player, "Successfully set this quarter's meta key " + key + " to " + value);
    }
}
