package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.QuarterDeleteCause;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EditMergeMethod extends CommandMethod {

    private static final Map<UUID, UUID> PENDING_MERGES = new ConcurrentHashMap<>();

    public EditMergeMethod() {
        super("merge", "quarters.command.quarters.edit.merge", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter currentQuarter = getQuarterAtPlayerOrThrow(player);

        if (!currentQuarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        UUID playerUUID = player.getUniqueId();
        UUID firstQuarterUUID = PENDING_MERGES.get(playerUUID);

        if (firstQuarterUUID == null) {
            PENDING_MERGES.put(playerUUID, currentQuarter.getUUID());
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.edit.merge.feedback.first_selected");
            return;
        }

        Quarter firstQuarter = QuarterManager.getInstance().getQuarter(firstQuarterUUID);
        PENDING_MERGES.remove(playerUUID);

        if (firstQuarter == null) throw new CommandMethodException("quarters.command.quarters.edit.merge.feedback.first_missing");
        if (firstQuarter.equals(currentQuarter)) throw new CommandMethodException("quarters.command.quarters.edit.merge.feedback.same_quarter");
        if (!firstQuarter.getTown().equals(currentQuarter.getTown())) throw new CommandMethodException("quarters.command.quarters.edit.merge.feedback.different_town");

        Confirmation.runOnAccept(() -> mergeQuarters(source, player, firstQuarter, currentQuarter))
                .setTitle(QuartersMessaging.translate(
                        player,
                        "quarters.command.quarters.edit.merge.confirmation.title",
                        Argument.string("first", firstQuarter.getName()),
                        Argument.string("second", currentQuarter.getName()),
                        Argument.string("cuboids", Integer.toString(currentQuarter.getCuboids().size()))
                ))
                .sendTo(player);
    }

    private void mergeQuarters(@NotNull CommandSourceStack source, @NotNull Player player, @NotNull Quarter firstQuarter, @NotNull Quarter secondQuarter) {
        if (!secondQuarter.delete(source.getSender(), QuarterDeleteCause.MERGE_COMMAND)) return;

        List<Cuboid> mergedCuboids = new ArrayList<>(firstQuarter.getCuboids());
        mergedCuboids.addAll(secondQuarter.getCuboids());
        firstQuarter.setCuboids(mergedCuboids);
        firstQuarter.save();

        QuartersMessaging.sendSuccessMessage(
                player,
                "quarters.command.quarters.edit.merge.feedback.success",
                Argument.string("first", firstQuarter.getName()),
                Argument.string("second", secondQuarter.getName())
        );
        QuartersMessaging.sendCommandFeedbackToTown(
                firstQuarter.getTown(),
                player,
                "quarters.command.quarters.edit.merge.feedback.town",
                firstQuarter.getFirstCornerOfFirstCuboid(),
                Argument.string("first", firstQuarter.getName()),
                Argument.string("second", secondQuarter.getName())
        );
    }
}
