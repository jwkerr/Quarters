package au.lupine.quarters.command.quarters.method.trust;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
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
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        String targetResidentName = getArgOrThrow(0, "No player name provided");
        Resident resident = TownyAPI.getInstance().getResident(targetResidentName);
        if (resident == null || resident.isNPC()) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_DOES_NOT_EXIST);

        UUID uuid = resident.getUUID();
        List<UUID> trusted = quarter.getTrusted();
        if (!trusted.contains(uuid)) {
            trusted.add(resident.getUUID());

            quarter.setTrusted(trusted);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, StringConstants.SPECIFIED_PLAYER_HAS_BEEN_ADDED_TO_THIS_QUARTERS_TRUSTED_LIST);
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has added " + resident.getName() + " to a quarter's trusted list", player.getLocation());
            if (resident.getPlayer() != null) QuartersMessaging.sendInfoMessage(resident.getPlayer(), "You have been trusted in a quarter", player.getLocation());
        } else {
            QuartersMessaging.sendErrorMessage(player, StringConstants.SPECIFIED_PLAYER_IS_ALREADY_TRUSTED_IN_THIS_QUARTER);
        }
    }
}
