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
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;

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

        Component trustedComponent = getTrustedComponent(quarter);

        String price;
        if (quarter.getPrice() == null) {
            price = "Not for sale";
        } else if (quarter.getPrice() == 0) {
            price = "Free";
        } else {
            price = TownyEconomyHandler.getFormattedBalance(quarter.getPrice());
        }

        TextComponent component = Component.text()
                .append(Component.text("Owner: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getOwner() == null ? "None " : quarter.getOwner().getName() + " ")).color(NamedTextColor.GRAY)
                .append(Component.text("Type: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getType().getFormattedName() + " ")).color(NamedTextColor.GRAY)
                .append(Component.text("Town: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getTown().getName() + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Price: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(price + " ")).color(NamedTextColor.GRAY)
                .append(Component.text("Embassy: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.isEmbassy() ? "True " : "False ")).color(NamedTextColor.GRAY)
                .append(Component.text("Cuboids: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getCuboids().size() + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Registered: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getRegistered() == null ? "N/A" : Instant.ofEpochMilli(quarter.getRegistered()).atZone(ZoneId.systemDefault()).toLocalDate() + " ")).color(NamedTextColor.GRAY)
                .append(Component.text("Claimed at: ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getClaimedAt() == null ? "N/A\n" : Instant.ofEpochMilli(quarter.getClaimedAt()).atZone(ZoneId.systemDefault()).toLocalDate() + "\n")).color(NamedTextColor.GRAY)
                .append(trustedComponent)
                .build();

        QuartersMessaging.sendInfoWall(player, component);
    }

    private Component getTrustedComponent(Quarter quarter) {
        Component trustedComponent = Component.empty()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("Trusted", TextColor.color(0x9655FF)))
                .append(Component.text("]", NamedTextColor.DARK_GRAY));

        Component hoverComponent;

        if (!quarter.getTrustedResidents().isEmpty() && quarter.getTrustedResidents() != null) {
            StringBuilder sb = new StringBuilder();
            for (Resident resident : quarter.getTrustedResidents()) {
                if (sb.length() > 0)
                    sb.append(", ");

                sb.append(resident.getName());
            }

            hoverComponent = Component.text(sb.toString()).color(NamedTextColor.GRAY);
        } else {
            hoverComponent = Component.text("None").color(NamedTextColor.GRAY);
        }

        return trustedComponent.hoverEvent(hoverComponent);
    }
}
