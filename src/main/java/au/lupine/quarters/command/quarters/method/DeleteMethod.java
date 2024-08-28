package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CopyOnWriteArrayList;

public class DeleteMethod extends CommandMethod {

    private boolean deleteAll = false;

    public DeleteMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.delete", true);
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
            Confirmation.runOnAccept(() -> {
                QuarterManager.getInstance().setQuarters(town, new CopyOnWriteArrayList<>());
                QuartersMessaging.sendSuccessMessage(player, "Successfully deleted all quarters in " + town.getName());
                QuartersMessaging.sendCommandFeedbackToTown(town, player, "has deleted all quarters in " + town.getName(), null);
            })
                    .setTitle("Are you sure you want to delete all the quarters in " + town.getName() + "?")
                    .sendTo(player);

            return;
        }

        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        quarter.delete();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_DELETED_THIS_QUARTER);
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "has deleted a quarter", player.getLocation());
    }
}
