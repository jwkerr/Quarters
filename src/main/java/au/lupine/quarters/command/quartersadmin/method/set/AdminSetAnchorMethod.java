package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class AdminSetAnchorMethod extends CommandMethod {

    public AdminSetAnchorMethod() {
        super("anchor", "quarters.command.quartersadmin.set.anchor");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Location location = player.getLocation();
        quarter.setAnchor(location);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.anchor.feedback.success");
    }
}
