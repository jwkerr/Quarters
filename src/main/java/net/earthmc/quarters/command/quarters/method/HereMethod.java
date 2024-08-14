package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.state.ActionType;
import net.earthmc.quarters.object.wrapper.Pair;
import net.earthmc.quarters.object.wrapper.QuarterPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HereMethod extends CommandMethod {

    public HereMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.here");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        TextComponent.Builder headerBuilder = Component.text();
        headerBuilder.append(Component.text(quarter.getName(), TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB())));
        headerBuilder.appendSpace();
        headerBuilder.append(getColourBadgeComponent(quarter.getColour()));
        headerBuilder.appendSpace();
        headerBuilder.append(getAnchorBadgeComponent(quarter.getAnchor()));

        Component header = headerBuilder.build();

        List<Pair<String, Component>> labelled = List.of(
                Pair.of("Owner", ConfigManager.getFormattedName(quarter.getOwner(), Component.text("None", NamedTextColor.GRAY))),
                Pair.of("Type", Component.text(quarter.getType().getCommonName(), NamedTextColor.GRAY)),
                Pair.of("Town", Component.text(quarter.getTown().getName(), NamedTextColor.GRAY).clickEvent(ClickEvent.runCommand("/towny:town " + quarter.getTown().getName()))),
                Pair.of("Price", getPriceComponent(quarter)),
                Pair.of("Embassy", Component.text(quarter.isEmbassy() ? "True" : "False", NamedTextColor.GRAY))
        );

        List<Pair<String, Component>> brackets = List.of(
                Pair.of("Stats", getStatsHoverComponent(quarter)),
                Pair.of("Trusted", getTrustedComponent(quarter)),
                Pair.of("Perms", getPermsComponent(quarter))
        );

        Component here = QuartersMessaging.getListComponent(header, labelled, brackets);

        QuartersMessaging.sendComponent(player, here);
    }

    private Component getColourBadgeComponent(@NotNull Color colour) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("✒", TextColor.color(colour.getRGB())));

        int r = colour.getRed();
        int g = colour.getGreen();
        int b = colour.getBlue();

        builder.hoverEvent(Component.text(r + ", " + g + ", " + b, TextColor.color(colour.getRGB()))
                .appendNewline()
                .append(Component.text("Click to copy command", NamedTextColor.GRAY))
        );

        builder.clickEvent(ClickEvent.copyToClipboard("/q set colour " + colour.getRed() + " " + colour.getGreen() + " " + colour.getBlue()));

        return builder.build();
    }

    private Component getAnchorBadgeComponent(@Nullable Location location) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("⚓", NamedTextColor.GRAY));

        if (location == null) {
            builder.hoverEvent(Component.text("No anchor set", NamedTextColor.GRAY));
            return builder.build();
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        builder.hoverEvent(Component.text("X=" + x + "/Y=" + y + "/Z=" + z, NamedTextColor.GRAY)
                .appendNewline()
                .append(Component.text("Click to copy coordinates", NamedTextColor.GRAY))
        );

        builder.clickEvent(ClickEvent.copyToClipboard(x + " " + y + " " + z));

        return builder.build();
    }

    private Component getPriceComponent(Quarter quarter) {
        String string;
        Double price = quarter.getPrice();

        if (price == null) {
            string = "Not for sale";
        } else if (price == 0) {
            string = "Free";
        } else {
            string = TownyEconomyHandler.getFormattedBalance(price);
        }

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text(string, NamedTextColor.GRAY));

        if (price != null) {
            builder.hoverEvent(Component.text("Click to claim!", NamedTextColor.GRAY));
            builder.clickEvent(ClickEvent.runCommand("/quarters:q claim " + quarter.getUUID()));
        }

        return builder.build();
    }

    private Component getStatsHoverComponent(Quarter quarter) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Cuboids: ", NamedTextColor.DARK_GRAY)).append(Component.text(quarter.getCuboids().size(), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Volume: ", NamedTextColor.DARK_GRAY)).append(Component.text(quarter.getVolume() + " blocks", NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Registered: ", NamedTextColor.DARK_GRAY)).append(Component.text(getFormattedDate(quarter.getRegistered()), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Claimed at: ", NamedTextColor.DARK_GRAY)).append(Component.text(getFormattedDate(quarter.getClaimedAt()), NamedTextColor.GRAY));

        return builder.build();
    }

    private Component getTrustedComponent(Quarter quarter) {
        List<Resident> trusted = quarter.getTrustedResidents();
        if (trusted.isEmpty()) return Component.text("None", NamedTextColor.GRAY);

        TextComponent.Builder builder = Component.text();
        List<Component> nameComponents = new ArrayList<>();
        for (Resident resident : trusted) {
            nameComponents.add(ConfigManager.getFormattedName(resident.getUUID(), null));
        }

        JoinConfiguration jc = JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY));
        builder.append(Component.join(jc, nameComponents));

        return builder.build();
    }

    private Component getPermsComponent(Quarter quarter) {
        QuarterPermissions permissions = quarter.getPermissions();

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Build: ", NamedTextColor.DARK_GRAY)).append(Component.text(permissions.createPermissionLine(ActionType.BUILD), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Destroy: ", NamedTextColor.DARK_GRAY)).append(Component.text(permissions.createPermissionLine(ActionType.DESTROY), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Switch: ", NamedTextColor.DARK_GRAY)).append(Component.text(permissions.createPermissionLine(ActionType.SWITCH), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Item use: ", NamedTextColor.DARK_GRAY)).append(Component.text(permissions.createPermissionLine(ActionType.ITEM_USE), NamedTextColor.GRAY));

        return builder.build();
    }

    private String getFormattedDate(Long timestamp) {
        if (timestamp == null) return "N/A";

        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.format(date);
    }
}
