package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class ColourCommand extends BaseCommand {
    @Subcommand("colour")
    @Description("Change particle outline colour of a quarter")
    @CommandPermission("quarters.command.quarters.colour")
    @CommandCompletion("@range:0-255 @range:0-255 @range:0-255")
    public void onColour(Player player, int r, int g, int b) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        if (!CommandUtil.hasPermissionOrMayorOrQuarterOwner(player, quarter, "quarters.action.colour"))
            return;

        int[] rgb = new int[]{r, g, b};

        if (!isRGBArrayValid(player, rgb))
            return;

        setQuarterColour(player, quarter, rgb);
    }

    public static boolean isRGBArrayValid(Player player, int[] rgb) {
        for (int colour : rgb) {
            if (colour < 0 || colour > 255) {
                QuartersMessaging.sendErrorMessage(player, "Specified int is out of range, value must be between 0-255");
                return false;
            }
        }

        return true;
    }

    public static void setQuarterColour(Player player, Quarter quarter, int[] rgb) {
        quarter.setRGB(rgb);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed this quarter's colour");
    }
}
