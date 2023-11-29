package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.SponsorCosmeticsManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

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

        String price;
        if (quarter.getPrice() == null) {
            price = "Not for sale";
        } else if (quarter.getPrice() == 0) {
            price = "Free";
        } else {
            price = TownyEconomyHandler.getFormattedBalance(quarter.getPrice());
        }

        int[] rgb = quarter.getRGB();

        TextComponent component = Component.text()
                .append(Component.text("Owner: ", NamedTextColor.DARK_GRAY))
                .append(getFormattedName(quarter.getOwnerResident()))
                .append(Component.text(" "))
                .append(Component.text("Type: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getType().getFormattedName() + " ", NamedTextColor.GRAY))
                .append(Component.text("Town: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getTown().getName() + "\n", NamedTextColor.GRAY))
                .append(Component.text("Price: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(price + " ", NamedTextColor.GRAY))
                .append(Component.text("Embassy: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.isEmbassy() ? "True " : "False ", NamedTextColor.GRAY))
                .append(Component.text("Cuboids: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getCuboids().size() + "\n", NamedTextColor.GRAY))
                .append(Component.text("Volume: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getVolume() + " blocks ", NamedTextColor.GRAY))
                .append(Component.text("Registered: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getRegistered() == null ? "N/A\n" : Instant.ofEpochMilli(quarter.getRegistered()).atZone(ZoneId.systemDefault()).toLocalDate().toString() + "\n")).color(NamedTextColor.GRAY)
                .append(Component.text("Claimed at: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(quarter.getClaimedAt() == null ? "N/A\n" : Instant.ofEpochMilli(quarter.getClaimedAt()).atZone(ZoneId.systemDefault()).toLocalDate().toString() + "\n", NamedTextColor.GRAY))
                .append(getTrustedComponent(quarter))
                .append(Component.text(" "))
                .append(getColourComponent(rgb)).clickEvent(ClickEvent.copyToClipboard("/q colour " + rgb[0] + " " + rgb[1] + " " + rgb[2]))
                .build();

        QuartersMessaging.sendComponentWithHeader(player, component);
    }

    private Component getSquareBracketComponent(String text) {
        return Component.empty()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text(text, TextColor.color(0x9655FF)))
                .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    private Component getColourComponent(int[] rgb) {
        Component colourComponent = getSquareBracketComponent("Colour");

        int colour = arrayToRGBHex(rgb);
        Component hoverComponent = Component.empty()
                .append(Component.text(String.valueOf(rgb[0]), TextColor.color(colour)))
                .append(Component.text(", ", NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(rgb[1]), TextColor.color(colour)))
                .append(Component.text(", ", NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(rgb[2]), TextColor.color(colour)))
                .append(Component.text("\nClick to copy command", NamedTextColor.GRAY));

        return colourComponent.hoverEvent(hoverComponent);
    }

    private int arrayToRGBHex(int[] rgb) {
        Color colour = new Color(rgb[0], rgb[1], rgb[2]);

        String hex = Integer.toHexString(colour.getRGB()).substring(2);

        return Integer.parseInt(hex, 16);
    }

    private Component getTrustedComponent(Quarter quarter) {
        Component trustedComponent = getSquareBracketComponent("Trusted");

        Component hoverComponent = Component.empty();

        if (!quarter.getTrustedResidents().isEmpty() && quarter.getTrustedResidents() != null) {
            int iteration = 0;
            for (Resident resident : quarter.getTrustedResidents()) {
                if (iteration != 0)
                    hoverComponent = hoverComponent.append(Component.text(", ", NamedTextColor.GRAY));

                hoverComponent = hoverComponent.append(getFormattedName(resident));

                iteration++;
            }
        } else {
            hoverComponent = Component.text("None", NamedTextColor.GRAY);
        }

        return trustedComponent.hoverEvent(hoverComponent);
    }

    private Component getFormattedName(Resident resident) {
        if (resident == null)
            return Component.text("None", NamedTextColor.GRAY);

        UUID uuid = resident.getUUID();
        String type = SponsorCosmeticsManager.sponsorMap.get(uuid);
        if (type != null) {
            switch (type) {
                case "authors":
                    return Component.text(resident.getName(), TextColor.color(0xF6003C));
                case "early_supporters":
                    return Component.text(resident.getName(), NamedTextColor.GOLD);
            }
        }

        return Component.text(resident.getName(), NamedTextColor.GRAY);
    }
}
