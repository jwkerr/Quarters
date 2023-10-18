package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.SelectionManager;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("quarters|q")
public class SelectionCommand extends BaseCommand {
    @Subcommand("selection")
    @Description("Manage your selections")
    @CommandPermission("quarters.command.quarters.selection")
    @CommandCompletion("add|clear|remove")
    public void onSelection(Player player, String arg) {
        switch (arg) {
            case "add":
                addCuboidToQuarter(player);
                break;
            case "clear":
                clearSelection(player);
                break;
            case "remove":
                removeSelection(player);
                break;
            default:
                QuartersMessaging.sendErrorMessage(player, "Invalid argument");
        }
    }

    private void addCuboidToQuarter(Player player) {
        Selection selection = SelectionManager.selectionMap.computeIfAbsent(player, k -> new Selection());
        List<Cuboid> cuboids = SelectionManager.cuboidsMap.computeIfAbsent(player, k -> new ArrayList<>());

        if (!CommandUtil.isSelectionValid(player, selection.getPos1(), selection.getPos2()))
            return;

        Cuboid newCuboid = new Cuboid(selection.getPos1(), selection.getPos2());
        if (!newCuboid.isEntirelyWithinTown() || newCuboid.isIntersectingPreexistingQuarter()) {
            QuartersMessaging.sendErrorMessage(player, "Selection is not in a valid location");
            return;
        }

        if (!cuboids.isEmpty()) {
            for (Cuboid cuboid : cuboids) {
                if (cuboid.doesIntersectWith(newCuboid)) {
                    QuartersMessaging.sendErrorMessage(player, "Could not add the current selection as it intersects with a cuboid that has already been added");
                    return;
                }
            }
        }

        selection.setPos1(null);
        selection.setPos2(null);

        cuboids.add(newCuboid);

        QuartersMessaging.sendSuccessMessage(player, "Successfully added cuboid to selection");
    }

    private void clearSelection(Player player) {
        Selection selection = SelectionManager.selectionMap.computeIfAbsent(player, k -> new Selection());

        selection.setPos1(null);
        selection.setPos2(null);

        List<Cuboid> cuboids = SelectionManager.cuboidsMap.computeIfAbsent(player, k -> new ArrayList<>());

        cuboids.clear();

        QuartersMessaging.sendInfoMessage(player, "Selection cleared");
    }

    private void removeSelection(Player player) {
        List<Cuboid> cuboids = SelectionManager.cuboidsMap.computeIfAbsent(player, k -> new ArrayList<>());

        for (Cuboid cuboid : cuboids) {
            if (cuboid.getPlayersInCuboid().contains(player)) {
                cuboids.remove(cuboid);
                QuartersMessaging.sendSuccessMessage(player, "Successfully removed the cuboid you are standing in from the selection");
                return;
            }
        }

        QuartersMessaging.sendErrorMessage(player, "Could not find any cuboid at your location to remove");
    }
}
