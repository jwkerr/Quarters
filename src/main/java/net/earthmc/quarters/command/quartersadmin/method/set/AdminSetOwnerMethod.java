package net.earthmc.quarters.command.quartersadmin.method.set;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSetOwnerMethod extends CommandMethod {

    public AdminSetOwnerMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.set.owner");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Resident resident = TownyAPI.getInstance().getResident(getArgOrThrow(0, StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED));
        if (resident == null || resident.isNPC()) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_DOES_NOT_EXIST);

        if (!quarter.isEmbassy() && !quarter.getTown().hasResident(resident)) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_COULD_NOT_BE_SET_AS_OWNER);

        quarter.setOwner(resident.getUUID());
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully set this quarter's owner to " + resident.getName());
    }
}
