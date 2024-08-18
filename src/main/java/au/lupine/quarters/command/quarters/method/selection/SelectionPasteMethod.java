package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SelectionPasteMethod extends CommandMethod {

    public SelectionPasteMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.selection.paste");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        List<Cuboid> vectors = SelectionCopyMethod.SELECTION_VECTOR_MAP.get(player);
        if (vectors == null) throw new CommandMethodException("You have not copied a selection, use /q selection copy");

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
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("Failed to paste clipboard as it will contain wilderness");
                case INTERSECTS -> throw new CommandMethodException("Failed to paste clipboard as it will intersect with pre-existing quarters");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("Failed to paste clipboard as it will span multiple towns");
                case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("Failed to paste clipboard as it is outside of this world's maximum or minimum height");
            }

            for (Cuboid currentCuboid : currentCuboids) {
                if (cuboidToPaste.intersectsWith(currentCuboid)) throw new CommandMethodException("Failed to paste clipboard as it intersects with your pre-existing selection");
            }
        }

        currentCuboids.addAll(toPaste);
        sm.setCuboids(player, currentCuboids);

        QuartersMessaging.sendSuccessMessage(player, "Successfully pasted your clipboard");
    }
}
