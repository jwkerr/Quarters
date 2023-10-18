package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.util.QuarterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

@CommandAlias("quarters|q")
public class InfoCommand extends BaseCommand {
    @Default
    @Subcommand("info")
    @Description("Display info about Quarters")
    @CommandPermission("quarters.command.quarters.info")
    public void onInfo(CommandSender sender) {
        PluginMeta meta = Quarters.INSTANCE.getPluginMeta();

        TextComponent component = Component.text()
                .append(Component.text("Author: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("Fruitloopins ")).color(NamedTextColor.GRAY)
                .append(Component.text("Version: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(meta.getVersion() + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Quarters: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(QuarterUtil.getAllQuarters().size())).color(NamedTextColor.GRAY)
                .build();

        QuartersMessaging.sendInfoWall(sender, component);
    }
}
