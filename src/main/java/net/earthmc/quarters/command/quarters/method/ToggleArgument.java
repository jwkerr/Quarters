package net.earthmc.quarters.command.quarters.method;

import net.earthmc.quarters.command.quarters.method.toggle.ToggleConstantOutlinesMethod;
import net.earthmc.quarters.command.quarters.method.toggle.ToggleEmbassyMethod;
import net.earthmc.quarters.object.base.CommandArgument;
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
        }
    }
}
