package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

@CommandAlias("quarters|q")
public class InfoCommand extends BaseCommand {
    @Default
    @Subcommand("info")
    @Description("Display info about Quarters")
    @CommandPermission("quarters.command.quarters.info")
    public void onInfo(CommandSender sender) {
        PluginMeta meta = Quarters.INSTANCE.getPluginMeta();

        int numCuboids = 0;
        for (Quarter quarter : QuarterUtil.getAllQuarters()) {
            numCuboids += quarter.getCuboids().size();
        }

        TextComponent component = Component.text()
                .append(Component.text("Author: ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Fruitloopins ", TextColor.color(0xF6003C)))
                .append(Component.text("Version: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(meta.getVersion() + "\n", NamedTextColor.GRAY))
                .append(Component.text("Quarters: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(QuarterUtil.getAllQuarters().size(), NamedTextColor.GRAY))
                .append(Component.text(" Cuboids: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(numCuboids + "\n", NamedTextColor.GRAY))
                .append(Component.text("Wiki", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/Fruitloopins/Quarters/wiki")))
                .append(Component.text(" "))
                .append(Component.text("Discord", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://discord.gg/ey6ZvnwAJp")))
                .build();

        QuartersMessaging.sendInfoWall(sender, component);
    }
}
