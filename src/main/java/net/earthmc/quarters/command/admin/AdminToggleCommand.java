package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.command.ToggleCommand;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quartersadmin|qa")
public class AdminToggleCommand extends BaseCommand {
    @Subcommand("toggle")
    @Description("Forcefully toggle quarters settings")
    @CommandPermission("quarters.command.quartersadmin.toggle")
    @CommandCompletion("embassy")
    public void onToggle(Player player, String arg) {
        switch (arg) {
            case "embassy":
                setQuarterAtLocationEmbassyStatus(player);
                break;
            default:
                QuartersMessaging.sendErrorMessage(player, "Invalid argument");
        }
    }

    private void setQuarterAtLocationEmbassyStatus(Player player) {
        if (!CommandUtil.hasPermission(player, "quarters.action.quartersadmin.embassy"))
            return;

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        ToggleCommand.toggleQuarterEmbassyStatus(player, quarter);
    }
}
