package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
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

        ResidentMetadataManager.getInstance().setParticleSize(resident, value);

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed your quarter particle size to " + value);
    }
}
