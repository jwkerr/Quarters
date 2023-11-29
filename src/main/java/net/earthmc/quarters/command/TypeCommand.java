package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("quarters|q")
public class TypeCommand extends BaseCommand {
    @Subcommand("type")
    @Description("Change a quarter's type")
    @CommandPermission("quarters.command.quarters.type")
    @CommandCompletion("apartment|commons|public|shop|station|worksite")
    public void onType(Player player, String type) {
        if (Arrays.stream(QuarterType.values()).noneMatch(e -> e.name().equalsIgnoreCase(type))) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return;
        }

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.type"))
            return;

        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        setQuarterType(player, quarter, QuarterType.getByName(type));
    }

    public static void setQuarterType(Player player, Quarter quarter, QuarterType quarterType) {
        quarter.setType(quarterType);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter has been set to type: " + quarter.getType().getFormattedName());
    }
}
