package net.earthmc.quarters.command.quarters.method;

import io.papermc.paper.plugin.configuration.PluginMeta;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class InfoMethod extends CommandMethod {

    public InfoMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.info");
    }

    @Override
    public void execute() {
        PluginMeta meta = Quarters.getInstance().getPluginMeta();

        int numQuarters = 0;
        int numCuboids = 0;
        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            numQuarters += 1;
            numCuboids += quarter.getCuboids().size();
        }

        List<Pair<String, Component>> labelledEntries = List.of(
                Pair.of("Author", ConfigManager.getFormattedName(UUID.fromString("fed0ec4a-f1ad-4b97-9443-876391668b34"), Component.text("Fruitloopins", NamedTextColor.GRAY))),
                Pair.of("Version", Component.text(meta.getVersion(), NamedTextColor.GRAY)),
                Pair.of("Quarters", Component.text(numQuarters, NamedTextColor.GRAY)),
                Pair.of("Cuboids", Component.text(numCuboids, NamedTextColor.GRAY))
        );

        Component info = QuartersMessaging.getListComponent(QuartersMessaging.PLUGIN_WORDMARK_COMPONENT.decorate(TextDecoration.UNDERLINED), labelledEntries, null).appendNewline()
                .append(Component.text("Wiki", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/jwkerr/Quarters/wiki"))).appendSpace()
                .append(Component.text("Discord", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://discord.gg/ey6ZvnwAJp")));

        QuartersMessaging.sendComponent(sender, info);
    }
}
