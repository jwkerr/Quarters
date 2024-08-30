package au.lupine.quarters.command.quarters.method.delete;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CopyOnWriteArrayList;

public class DeleteAllMethod extends CommandMethod {

    public DeleteAllMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.delete.all", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        Confirmation.runOnAccept(() -> {
            QuarterManager.getInstance().setQuarters(town, new CopyOnWriteArrayList<>());
            QuartersMessaging.sendSuccessMessage(player, "Successfully deleted all quarters in " + town.getName());
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has deleted all quarters in " + town.getName(), null);
        }).setTitle("Are you sure you want to delete all the quarters in " + town.getName() + "?")
                .sendTo(player);
    }
}
