package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.ResidentMetadataManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class WandMethod extends CommandMethod {

    public WandMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.wand");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();

        boolean hasReceivedFreeWand = rmm.hasReceivedFreeWand(resident);
        if (hasReceivedFreeWand) throw new CommandMethodException("You have already received a free Quarters wand");

        HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(new ItemStack(ConfigManager.getWandMaterial()));
        if (!remaining.isEmpty()) throw new CommandMethodException("Please open a slot in your inventory and use the command again");

        rmm.setHasReceivedFreeWand(resident, true);

        QuartersMessaging.sendSuccessMessage(player, "Enjoy your free wand! Read the wiki on /q for help");
    }
}
