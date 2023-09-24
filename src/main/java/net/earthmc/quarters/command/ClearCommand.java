package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class ClearCommand extends BaseCommand {
    @Subcommand("clear")
    @Description("Clear current Quarters selection")
    @CommandPermission("quarters.command.clear")
    public void onClear(Player player) {
        Selection selection = SelectionManager.selectionMap.computeIfAbsent(player, k -> new Selection());

        selection.setPos1(null);
        selection.setPos2(null);

        QuartersMessaging.sendInfoMessage(player, "Selection cleared");
    }
}
