package net.earthmc.quarters.command.quarters.method.selection;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.SelectionManager;
import net.earthmc.quarters.object.base.CommandMethod;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectionClearMethod extends CommandMethod {

    public SelectionClearMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.selection.clear");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        SelectionManager sm = SelectionManager.getInstance();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendSuccessMessage(player, "Selection cleared");
    }
}
