package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
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
