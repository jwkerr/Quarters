package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.utils.QuarterUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SelectionManager {
    public static HashMap<Player, Selection> selectionMap = new HashMap<>();

    public static void selectPosition(Player player, Location location, boolean isPos1) {
        Selection selection = selectionMap.computeIfAbsent(player, k -> new Selection());

        Town town = TownyAPI.getInstance().getTown(location);
        if (!canSelectPosition(player, town))
            return;

        if (isPos1)
            selection.setPos1(location);

        if (!isPos1)
            selection.setPos2(location);

        QuartersMessaging.sendInfoMessage(player, getSelectedPositionComponent(isPos1, location));
    }

    public static boolean canSelectPosition(Player player, Town town) {
        if (town == null) {
            QuartersMessaging.sendErrorMessage(player, "Selected area must be part of a town");
            return false;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return false;

        if (!QuarterUtils.hasSufficientPerms(player, town)) {
            QuartersMessaging.sendErrorMessage(player, "Selected area must be a town where you have sufficient permissions");
            return false;
        }

        return true;
    }

    private static Component getSelectedPositionComponent(boolean pos1, Location location) {
        int pos = pos1 ? 1 : 2;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return Component.text()
                .append(Component.text(String.format("Position %s: ", pos)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(Component.text("X=" + x).color(NamedTextColor.RED)).resetStyle()
                .append(Component.text("/").color(NamedTextColor.GRAY))
                .append(Component.text("Y=" + y).color(NamedTextColor.GREEN))
                .append(Component.text("/").color(NamedTextColor.GRAY))
                .append(Component.text("Z=" + z).color(NamedTextColor.BLUE))
                .build();
    }
}
