package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.command.ColourCommand;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quartersadmin|qa")
public class AdminColourCommand extends BaseCommand {
    @Subcommand("colour")
    @Description("Forcefully change particle outline colour of a quarter")
    @CommandPermission("quarters.command.quartersadmin.colour")
    @CommandCompletion("@range:0-255 @range:0-255 @range:0-255")
    public void onColour(Player player, int r, int g, int b) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        int[] rgb = new int[]{r, g, b};

        if (!ColourCommand.isRGBArrayValid(player, rgb))
            return;

        ColourCommand.setQuarterColour(player, quarter, rgb);
    }
}
