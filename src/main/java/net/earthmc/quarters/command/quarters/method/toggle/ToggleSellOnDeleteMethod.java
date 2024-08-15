package net.earthmc.quarters.command.quarters.method.toggle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.TownMetadataManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleSellOnDeleteMethod extends CommandMethod {

    public ToggleSellOnDeleteMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle.sellondelete");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        TownMetadataManager tmm = TownMetadataManager.getInstance();
        boolean sellOnDelete = tmm.getSellOnDelete(town);

        tmm.setSellOnDelete(town, !sellOnDelete);

        if (!sellOnDelete) {
            QuartersMessaging.sendSuccessMessage(player, "Quarters will now be set for sale when the owner is deleted by Towny");
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has toggled quarters in " + town.getName() + " to be automatically sold when the owner is deleted by Towny", player.getLocation());
        } else {
            QuartersMessaging.sendSuccessMessage(player, "Quarters will no longer be set for sale when the owner is deleted by Towny");
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has toggled quarters in " + town.getName() + " to no longer be automatically sold when the owner is deleted by Towny", player.getLocation());
        }
    }
}
