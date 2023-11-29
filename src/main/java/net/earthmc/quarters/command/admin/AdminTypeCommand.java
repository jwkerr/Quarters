package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.command.TypeCommand;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("quartersadmin|qa")
public class AdminTypeCommand extends BaseCommand {
    @Subcommand("type")
    @Description("Change a quarter's type")
    @CommandPermission("quarters.command.quartersadmin.type")
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

        TypeCommand.setQuarterType(player, quarter, QuarterType.getByName(type));
    }
}
