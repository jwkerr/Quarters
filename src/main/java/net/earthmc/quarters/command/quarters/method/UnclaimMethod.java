package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.QuarterManager;
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
        QuarterManager qm = QuarterManager.getInstance();

        if (!qm.isPlayerInQuarter(player)) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_STANDING_WITHIN_A_QUARTER);

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        if (!resident.equals(quarter.getOwnerResident())) throw new CommandMethodException(StringConstants.YOU_DO_NOT_OWN_THIS_QUARTER);

        quarter.setOwner(null);

        QuartersMessaging.sendSuccessMessage(player, "You have successfully unclaimed this quarter");
    }
}
