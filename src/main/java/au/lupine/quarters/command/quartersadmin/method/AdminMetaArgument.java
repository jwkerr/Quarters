package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.command.quartersadmin.method.meta.AdminMetaRemoveMethod;
import au.lupine.quarters.command.quartersadmin.method.meta.AdminMetaSetMethod;
import au.lupine.quarters.object.base.CommandArgument;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.Pair;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminMetaArgument extends CommandArgument {

    public AdminMetaArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.meta");
    }

    @Override
    public void execute() {
        if (args.length == 0) {
            sendQuarterAtLocationMeta();
            return;
        }

        parseMethod(sender, args[0].toLowerCase(), CommandMethod.removeFirstArgument(args));
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "set" -> new AdminMetaSetMethod(sender, args).execute();
            case "remove" -> new AdminMetaRemoveMethod(sender, args).execute();
        }
    }

    private void sendQuarterAtLocationMeta() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        List<Pair<String, Component>> labelledEntries = new ArrayList<>();
        for (CustomDataField<?> cdf : quarter.getMetadata()) {
            Object value = cdf.getValue();
            labelledEntries.add(Pair.of(cdf.getKey(), Component.text(value == null ? "null" : value.toString(), NamedTextColor.GRAY)));
        }

        Component header = Component.text(quarter.getUUID() + "'s meta", TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB()))
                .clickEvent(ClickEvent.copyToClipboard(getLabelledEntriesAsUglyPrint(labelledEntries)))
                .hoverEvent(Component.text("Click to copy meta", NamedTextColor.GRAY));

        Component listComponent = QuartersMessaging.getListComponent(header, labelledEntries, null);
        QuartersMessaging.sendComponent(player, listComponent);
    }

    private String getLabelledEntriesAsUglyPrint(List<Pair<String, Component>>  entries) {
        List<String> lines = new ArrayList<>();

        for (Pair<String, Component> entry : entries) {
            String plainText = PlainTextComponentSerializer.plainText().serialize(entry.getSecond());

            lines.add(entry.getFirst() + " = " + plainText);
        }

        return String.join("\n", lines);
    }
}
