package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SelectionCopyMethod extends CommandMethod {

    public static final Map<UUID, List<Cuboid>> SELECTION_VECTOR_MAP = new ConcurrentHashMap<>();

    public SelectionCopyMethod() {
        super("copy", "quarters.command.quarters.selection.copy");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);
        if (cuboids.isEmpty()) throw new CommandMethodException(StringConstants.YOU_HAVE_NOT_SELECTED_ANY_AREAS);

        Location location = player.getLocation();

        List<Cuboid> vector = new ArrayList<>();
        for (Cuboid cuboid : cuboids) {
            vector.add(cuboid.subtract(location));
        }

        SELECTION_VECTOR_MAP.put(player.getUniqueId(), vector);

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.selection.copy.feedback.success");
    }
}
