package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
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
