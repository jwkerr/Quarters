package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("quarters|q")
public class TypeCommand extends BaseCommand {
    @Subcommand("type")
    @Description("Change a quarter's type")
    @CommandPermission("quarters.command.quarters.type")
    @CommandCompletion("apartment|shop|station")
    public void onType(Player player, String type) {
        if (Arrays.stream(QuarterType.values()).noneMatch(e -> e.name().equalsIgnoreCase(type))) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return;
        }

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        assert quarter != null;

        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.type"))
            return;

        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        quarter.setType(QuarterType.getByName(type));
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter has been set to type: " + quarter.getType().getFormattedName());
    }
}
