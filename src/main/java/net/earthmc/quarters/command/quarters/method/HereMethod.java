package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.Pair;
import net.earthmc.quarters.object.wrapper.StringConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        QuarterManager qm = QuarterManager.getInstance();
        if (!qm.isPlayerInQuarter(player)) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_STANDING_WITHIN_A_QUARTER);

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) return;

        Color colour = quarter.getColour();
        Component header = Component.text(quarter.getName(), TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB())).appendSpace()
                .append(Component.text("\uD83C\uDFA8", TextColor.color(colour.getRGB()))
                        .hoverEvent(getColourHoverComponent(colour))
                        .clickEvent(ClickEvent.copyToClipboard("/q set colour " + colour.getRed() + " " + colour.getGreen() + " " + colour.getBlue()))
                );

        List<Pair<String, Component>> labelled = List.of(
                Pair.of("Owner", ConfigManager.getFormattedName(quarter.getOwner(), Component.text("None", NamedTextColor.GRAY))),
                Pair.of("Type", Component.text(quarter.getType().getCommonName(), NamedTextColor.GRAY)),
                Pair.of("Town", Component.text(quarter.getTown().getName(), NamedTextColor.GRAY)),
                Pair.of("Price", Component.text(getPriceString(quarter.getPrice()), NamedTextColor.GRAY)),
                Pair.of("Embassy", Component.text(quarter.isEmbassy() ? "True" : "False", NamedTextColor.GRAY))
        );

        List<Pair<String, Component>> brackets = List.of(
                Pair.of("Stats", getStatsHoverComponent(quarter)),
                Pair.of("Trusted", getTrustedComponent(quarter))
        );

        Component here = QuartersMessaging.getListComponent(header, labelled, brackets);

        QuartersMessaging.sendComponent(player, here);
    }

    private Component getColourHoverComponent(Color colour) {
        TextColor textColour = TextColor.color(colour.getRGB());
        Component separator = Component.text(", ", NamedTextColor.GRAY);

        return Component.text(colour.getRed(), textColour).append(separator)
                .append(Component.text(colour.getBlue(), textColour)).append(separator)
                .append(Component.text(colour.getBlue(), textColour)).appendNewline()
                .append(Component.text("Click to copy command"));
    }

    private String getPriceString(Double price) {
        if (price == null) return "Not for sale";
        if (price == 0) return "Free";

        return TownyEconomyHandler.getFormattedBalance(price);
    }

    private Component getStatsHoverComponent(Quarter quarter) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Cuboids: ", NamedTextColor.DARK_GRAY)).append(Component.text(quarter.getCuboids().size(), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text("Volume: ", NamedTextColor.DARK_GRAY)).append(Component.text(quarter.getVolume(), NamedTextColor.GRAY));
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
            nameComponents.add(ConfigManager.getFormattedName(resident.getUUID(), Component.text("Unavailable", NamedTextColor.GRAY)));
        }

        JoinConfiguration jc = JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY));
        builder.append(Component.join(jc, nameComponents));

        return builder.build();
    }

    private String getFormattedDate(Long timestamp) {
        if (timestamp == null) return "N/A";

        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.format(date);
    }
}
