package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.event.QuarterDeleteEvent;
import au.lupine.quarters.command.quarters.method.delete.DeleteAllMethod;
import au.lupine.quarters.command.quarters.method.delete.DeletePlotMethod;
import au.lupine.quarters.object.base.CommandArgument;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteArgument extends CommandArgument {

    public DeleteArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.delete", true);
    }

    @Override
    public void execute() {
        if (args.length == 0) {
            deleteQuarterAtLocation();
            return;
        }

        parseMethod(sender, args[0].toLowerCase(), CommandMethod.removeFirstArgument(args));
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "all" -> new DeleteAllMethod(sender, args).execute();
            case "plot" -> new DeletePlotMethod(sender, args).execute();
        }
    }

    private void deleteQuarterAtLocation() {
        Player player = getSenderAsPlayerOrThrow();

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        boolean deleted = quarter.delete(QuarterDeleteEvent.Cause.COMMAND, player);
        if (deleted) {
            QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_DELETED_THIS_QUARTER);
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has deleted a quarter", player.getLocation());
        }
    }
}
