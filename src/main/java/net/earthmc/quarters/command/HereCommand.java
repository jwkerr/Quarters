package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class HereCommand extends BaseCommand {
    @Subcommand("here")
    @Description("Get info about the quarter you are standing in")
    @CommandPermission("quarters.command.quarters.here")
    public void onHere(Player player) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        String trustedString = "None";
        if (!quarter.getTrustedResidents().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Resident resident : quarter.getTrustedResidents()) {
                if (sb.length() > 0)
                    sb.append(", ");

                sb.append(resident.getName());
            }

            trustedString = sb.toString();
        }

        String price;
        if (quarter.getPrice() == null) {
            price = "Not for sale";
        } else if (quarter.getPrice() == 0) {
            price = "Free";
        } else {
            price = TownyEconomyHandler.getFormattedBalance(quarter.getPrice());
        }

        TextComponent component = Component.text()
                .append(Component.text("Owner: ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                .append(Component.text(quarter.getOwner() == null ? "None\n" : quarter.getOwner().getName() + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Trusted: ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                .append(Component.text(trustedString + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Price: ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                .append(Component.text(price + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Type: ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                .append(Component.text(quarter.getType().getFormattedName())).color(NamedTextColor.GRAY)
                .build();

        QuartersMessaging.sendInfoWall(player, component);
    }
}
