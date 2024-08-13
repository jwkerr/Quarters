package net.earthmc.quarters.command.quarters.method.trust;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TrustRemoveMethod extends CommandMethod {

    public TrustRemoveMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.trust.remove");
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
        if (trusted.contains(uuid)) {
            trusted.remove(uuid);

            quarter.setTrusted(trusted);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "Specified player has been removed from this quarter's trusted list");
        } else {
            QuartersMessaging.sendErrorMessage(player, "Specified player is not trusted in this quarter");
        }
    }
}
