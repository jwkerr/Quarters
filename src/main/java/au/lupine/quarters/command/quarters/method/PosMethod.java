package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.SelectionType;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PosMethod extends CommandMethod {

    public PosMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.pos");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        String arg = getArgOrThrow(0, "No position provided");

        SelectionType type = switch (arg) {
            case "one" -> SelectionType.LEFT;
            case "two" -> SelectionType.RIGHT;
            default -> throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID + arg);
        };

        int adjustX = Integer.parseInt(getArgOrDefault(1, "0"));
        int adjustY = Integer.parseInt(getArgOrDefault(2, "0"));;
        int adjustZ = Integer.parseInt(getArgOrDefault(3, "0"));;

        Location location = player.getLocation();
        location.add(adjustX, adjustY, adjustZ);

        SelectionManager.getInstance().selectPosition(player, location, type);
    }
}
