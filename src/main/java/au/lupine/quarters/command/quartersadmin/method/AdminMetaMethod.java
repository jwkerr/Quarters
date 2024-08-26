package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.Pair;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminMetaMethod extends CommandMethod {

    public AdminMetaMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.meta");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Component header = Component.text(quarter.getUUID() + "'s meta", TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB()));

        List<Pair<String, Component>> labelledEntries = new ArrayList<>();
        for (CustomDataField<?> cdf : quarter.getMetadata()) {
            Object value = cdf.getValue();
            labelledEntries.add(Pair.of(cdf.getKey(), Component.text(value == null ? "null" : value.toString(), NamedTextColor.GRAY)));
        }

        Component listComponent = QuartersMessaging.getListComponent(header, labelledEntries, null);
        QuartersMessaging.sendComponent(player, listComponent);
    }
}
