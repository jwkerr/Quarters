package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class AdminEvictMethod extends CommandMethod {

    public AdminEvictMethod() {
        super("evict", "quarters.command.quartersadmin.evict");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        UUID owner = quarter.getOwner();
        if (owner == null) throw new CommandMethodException("quarters.command.quarters.evict.feedback.no_owner");

        quarter.evictOwner();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.evict.feedback.success");
    }
}
