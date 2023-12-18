package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

/**
 * @Description: 回家命令
 * @ClassName: HomeCommand
 * @Author: ice_light
 * @Date: 2023/12/18 17:15
 * @Version: 1.0
 */
@CommandAlias("quarters|q")
public class HomeCommand extends BaseCommand {
    @Subcommand("home")
    @Description("go home")
    @CommandPermission("quarters.command.quarters.home")
    public void onHome(Player player) {
        Quarter quarter = QuarterUtil.playerHasQuarters(player);
        if(quarter == null) {
            QuartersMessaging.sendErrorMessage(player, "您还没有入住公寓，可以输入/q auto租用一间公寓");
            return;
        }
        player.teleportAsync(quarter.getCuboids().get(0).getCentorLocation());
    }
}
