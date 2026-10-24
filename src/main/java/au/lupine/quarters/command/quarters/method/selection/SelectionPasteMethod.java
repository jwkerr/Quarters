package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.exception.MultiCommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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

        SelectionCopyMethod.Clipboard clipboard = SelectionCopyMethod.CLIPBOARD_MAP.get(player.getUniqueId());
        if (clipboard == null) throw new CommandMethodException("quarters.command.quarters.selection.paste.feedback.no_clipboard");

        List<Cuboid> toPaste = clipboard.pasteAt(player.getLocation());

        SelectionManager sm = SelectionManager.getInstance();
        List<Cuboid> currentCuboids = sm.getCuboids(player);
        List<CommandMethodException> validationErrors = new ArrayList<>();

        for (int i = 0; i < toPaste.size(); i++) {
            Cuboid cuboidToPaste = toPaste.get(i);
            for (CuboidValidity validity : cuboidToPaste.checkValidity()) {
                switch (validity) {
                    case CONTAINS_WILDERNESS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.contains_wilderness"));
                    case INTERSECTS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.intersects"));
                    case SPANS_MULTIPLE_TOWNS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.spans_multiple_towns"));
                    case OUTSIDE_WORLD_BOUNDS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.outside_world_bounds"));
                    case TOO_LARGE -> validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.too_large"));
                    case VALID -> { }
                }
            }

            for (Cuboid currentCuboid : currentCuboids) {
                if (cuboidToPaste.intersectsWith(currentCuboid)) {
                    validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.intersects_selection"));
                }
            }

            for (int copiedIndex = 0; copiedIndex < i; copiedIndex++) {
                if (cuboidToPaste.intersectsWith(toPaste.get(copiedIndex))) {
                    validationErrors.add(new CommandMethodException("quarters.command.quarters.selection.paste.feedback.intersects_selection"));
                }
            }
        }

        if (!validationErrors.isEmpty()) throw new MultiCommandMethodException(validationErrors);

        currentCuboids.addAll(toPaste);
        sm.setCuboids(player, currentCuboids);

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.selection.paste.feedback.success");
    }
}
