package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.toggle.*;
import au.lupine.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class ToggleArgument extends CommandArgument {

    public ToggleArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "constantoutlines" -> new ToggleConstantOutlinesMethod(sender, args).execute();
            case "embassy" -> new ToggleEmbassyMethod(sender, args).execute();
            case "entryblinking" -> new ToggleEntryBlinkingMethod(sender, args).execute();
            case "entrynotifications" -> new ToggleEntryNotificationsMethod(sender, args).execute();
            case "sellondelete" -> new ToggleSellOnDeleteMethod(sender, args).execute();
        }
    }
}
