package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class AdminDeleteMethod extends CommandMethod {

    public AdminDeleteMethod() {
        super("delete", "quarters.command.quartersadmin.delete");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        quarter.delete();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_DELETED_THIS_QUARTER);
    }
}
