package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SelectionPasteMethod extends CommandMethod {

    public SelectionPasteMethod() {
        super("paste", "quarters.command.quarters.selection.paste");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        List<Cuboid> vectors = SelectionCopyMethod.SELECTION_VECTOR_MAP.get(player.getUniqueId());
        if (vectors == null) throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.no_clipboard");

        Location location = player.getLocation();

        List<Cuboid> toPaste = new ArrayList<>();
        for (Cuboid vector : vectors) {
            Cuboid addCuboid = vector.add(location);
            toPaste.add(addCuboid);
        }

        SelectionManager sm = SelectionManager.getInstance();
        List<Cuboid> currentCuboids = sm.getCuboids(player);

        for (Cuboid cuboidToPaste : toPaste) {
            CuboidValidity validity = cuboidToPaste.checkValidity();
            switch (validity) {
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.contains_wilderness");
                case INTERSECTS -> throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.intersects");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.spans_multiple_towns");
                case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.outside_world_bounds");
                case TOO_LARGE -> throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.too_large");
            }

            for (Cuboid currentCuboid : currentCuboids) {
                if (cuboidToPaste.intersectsWith(currentCuboid)) throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.intersects_selection");
            }
        }

        currentCuboids.addAll(toPaste);
        sm.setCuboids(player, currentCuboids);

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.selection.paste.feedback.success");
    }
}
