package net.earthmc.quarters.command.quarters.method.selection;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.SelectionManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SelectionCopyMethod extends CommandMethod {

    public static final Map<Player, List<Cuboid>> SELECTION_VECTOR_MAP = new ConcurrentHashMap<>();

    public SelectionCopyMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.selection.copy");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);
        if (cuboids.isEmpty()) throw new CommandMethodException(StringConstants.YOU_HAVE_NOT_SELECTED_ANY_AREAS);

        Location location = player.getLocation();

        List<Cuboid> vector = new ArrayList<>();
        for (Cuboid cuboid : cuboids) {
            vector.add(cuboid.subtract(location));
        }

        SELECTION_VECTOR_MAP.put(player, vector);

        QuartersMessaging.sendSuccessMessage(player, "Successfully copied your selection to your clipboard, it is relative to your location");
    }
}
