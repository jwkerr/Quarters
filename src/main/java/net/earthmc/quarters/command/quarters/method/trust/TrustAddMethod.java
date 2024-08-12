package net.earthmc.quarters.command.quarters.method.trust;

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

import java.util.List;
import java.util.UUID;

public class TrustAddMethod extends CommandMethod {

    public TrustAddMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.trust.add");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        QuarterManager qm = QuarterManager.getInstance();
        if (!qm.isPlayerInQuarter(player)) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_STANDING_WITHIN_A_QUARTER);

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) return;

        if (!quarter.hasPermission(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        String targetResidentName = getArgOrThrow(0, "No player name provided");
        Resident resident = TownyAPI.getInstance().getResident(targetResidentName);
        if (resident == null || resident.isNPC()) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_DOES_NOT_EXIST);

        UUID uuid = resident.getUUID();
        List<UUID> trusted = quarter.getTrusted();
        if (!trusted.contains(uuid)) {
            trusted.add(resident.getUUID());

            quarter.setTrusted(trusted);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "Specified player has been added to this quarter's trusted list");
        } else {
            QuartersMessaging.sendErrorMessage(player, "Specified player is already trusted in this quarter");
        }
    }
}
