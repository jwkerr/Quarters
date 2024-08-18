package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class UnclaimMethod extends CommandMethod {

    public UnclaimMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.unclaim");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        if (!quarter.isResidentOwner(resident)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_OWN_THIS_QUARTER);

        quarter.setOwner(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "You have successfully unclaimed this quarter");
    }
}
