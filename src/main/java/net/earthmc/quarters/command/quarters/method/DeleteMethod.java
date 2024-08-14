package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.TownMetadataManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DeleteMethod extends CommandMethod {

    private boolean deleteAll = false;

    public DeleteMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.delete");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        String arg = getArgOrNull(0);
        if (arg != null) {
            if (arg.equals("all")) {
                deleteAll = true;
            } else {
                throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID + arg);
            }
        }

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        if (deleteAll) {
            TownMetadataManager.getInstance().setQuarterList(town, new ArrayList<>());
            QuartersMessaging.sendSuccessMessage(player, "Successfully deleted all quarters in " + town.getName());
            return;
        }

        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        quarter.delete();

        QuartersMessaging.sendSuccessMessage(player, "Successfully deleted this quarter");
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "has deleted a quarter", player.getLocation());
    }
}
