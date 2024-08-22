package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetParticleSizeMethod extends CommandMethod {

    public SetParticleSizeMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.particlesize");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        String arg = getArgOrThrow(0, StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);

        float value;
        try {
            value = Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID + arg);
        }

        if (value < 0.0F || value > 4.0F) throw new CommandMethodException("Provided value is invalid, please provide a value between 0.0 and 4.0");

        Quarter quarter = getQuarterAtPlayerOrNull(player);

        if (quarter != null && quarter.hasBasicCommandPermissions(player)) {
            quarter.setParticleSize(value);

            QuartersMessaging.sendSuccessMessage(player, "Successfully changed this quarter's particle size to " + value);
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has changed a quarter's particle size to " + value, player.getLocation());
            return;
        }

        ResidentMetadataManager.getInstance().setParticleSize(resident, value);
        QuartersMessaging.sendSuccessMessage(player, "Successfully changed your default quarter particle size to " + value);
    }
}
