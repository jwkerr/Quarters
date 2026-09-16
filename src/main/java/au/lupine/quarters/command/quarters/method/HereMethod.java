package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.wrapper.Pair;
import au.lupine.quarters.object.wrapper.QuarterPermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class HereMethod extends CommandMethod {

    public HereMethod() {
        super("here", "quarters.command.quarters.here");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("quarter", StringArgumentType.word())
                        .suggests((context, builder) -> suggestQuarterUUIDs(builder))
                        .executes(context -> run(context.getSource(), context.getArgument("quarter", String.class))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        execute(source, null);
    }

    private int run(@NotNull CommandSourceStack source, @Nullable String quarterArgument) {
        try {
            execute(source, quarterArgument);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(source.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    private void execute(@NotNull CommandSourceStack source, @Nullable String quarterArgument) {
        Player player = getSenderAsPlayerOrThrow(source);

        Quarter quarter = getQuarterAtPlayerOrByUUIDOrThrow(player, quarterArgument);

        UUID owner = quarter.getOwner();
        boolean catMode = owner != null && ConfigManager.getUserGroupOrDefault(owner, ConfigManager.DEFAULT_USER_GROUP).hasCatMode();

        TextComponent.Builder headerBuilder = Component.text();
        headerBuilder.append(Component.text(quarter.getName(), TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB())));
        headerBuilder.appendSpace();
        headerBuilder.append(getColourBadgeComponent(quarter.getColour(), catMode));
        headerBuilder.appendSpace();
        headerBuilder.append(getAnchorBadgeComponent(quarter.getAnchor(), catMode));

        if (catMode) {
            headerBuilder.appendSpace();
            headerBuilder.append(getCatBadgeComponent(quarter));
        }

        Component header = headerBuilder.build();

        List<Pair<String, Component>> labelled = List.of(
                Pair.of(labelKey(catMode, "owner"), ConfigManager.getFormattedName(quarter.getOwner(), Component.translatable("quarters.common.none", NamedTextColor.GRAY))),
                Pair.of(labelKey(catMode, "type"), Component.text(quarter.getType().getCommonName(), NamedTextColor.GRAY)),
                Pair.of(labelKey(catMode, "town"), Component.text(quarter.getTown().getName(), NamedTextColor.GRAY).clickEvent(ClickEvent.runCommand("/towny:town " + quarter.getTown().getName()))),
                Pair.of(labelKey(catMode, "price"), getPriceComponent(quarter, catMode)),
                Pair.of(labelKey(catMode, "embassy"), Component.translatable(quarter.isEmbassy() ? "quarters.common.true" : "quarters.common.false", NamedTextColor.GRAY))
        );

        List<Pair<String, Component>> brackets = List.of(
                Pair.of(labelKey(catMode, "stats"), getStatsHoverComponent(quarter, catMode)),
                Pair.of(labelKey(catMode, "trusted"), getTrustedComponent(quarter, catMode)),
                Pair.of(labelKey(catMode, "perms"), getPermsComponent(quarter))
        );

        Component here = QuartersMessaging.getListComponent(header, labelled, brackets);

        QuartersMessaging.sendComponent(player, here);
    }

    private Component getColourBadgeComponent(@NotNull Color colour, boolean catMode) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("✒", TextColor.color(colour.getRGB())));

        int r = colour.getRed();
        int g = colour.getGreen();
        int b = colour.getBlue();

        builder.hoverEvent(Component.text(r + ", " + g + ", " + b, TextColor.color(colour.getRGB()))
                .appendNewline()
                .append(Component.translatable(catKey(catMode, "colour_badge.hover"), NamedTextColor.GRAY))
        );

        builder.clickEvent(ClickEvent.copyToClipboard("/q set colour " + colour.getRed() + " " + colour.getGreen() + " " + colour.getBlue()));

        return builder.build();
    }

    private Component getAnchorBadgeComponent(@Nullable Location location, boolean catMode) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("⚓", NamedTextColor.GRAY));

        if (location == null) {
            builder.hoverEvent(Component.translatable(catKey(catMode, "anchor_badge.no_anchor"), NamedTextColor.GRAY));
            return builder.build();
        }

        builder.hoverEvent(QuartersMessaging.getLocationComponent(location)
                .appendNewline()
                .append(Component.translatable(catKey(catMode, "anchor_badge.hover"), NamedTextColor.GRAY))
        );

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        builder.clickEvent(ClickEvent.copyToClipboard(x + " " + y + " " + z));

        return builder.build();
    }

    private Component getCatBadgeComponent(@NotNull Quarter quarter) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("\uD83D\uDE39", NamedTextColor.YELLOW));

        builder.hoverEvent(Component.translatable("quarters.command.quarters.here.cat_badge.hover", NamedTextColor.GRAY));
        builder.clickEvent(ClickEvent.runCommand("/quarters:q meow " + quarter.getUUID()));

        return builder.build();
    }

    private Component getPriceComponent(Quarter quarter, boolean catMode) {
        String string;
        Double price = quarter.getPrice();

        if (price == null) {
            return Component.translatable(catKey(catMode, "price.not_for_sale"), NamedTextColor.GRAY);
        } else if (price == 0) {
            string = catKey(catMode, "price.free");
        } else {
            string = TownyEconomyHandler.getFormattedBalance(price);
        }

        TextComponent.Builder builder = Component.text();
        builder.append(price == 0 ? Component.translatable(string, NamedTextColor.GRAY) : Component.text(string, NamedTextColor.GRAY));

        if (price != null) {
            builder.hoverEvent(Component.translatable(catKey(catMode, "price.hover"), NamedTextColor.GRAY));
            builder.clickEvent(ClickEvent.runCommand("/quarters:q claim " + quarter.getUUID()));
        }

        return builder.build();
    }

    private Component getStatsHoverComponent(Quarter quarter, boolean catMode) {
        TextComponent.Builder builder = Component.text();
        builder.append(statLabel(catMode, "cuboids")).append(Component.text(quarter.getCuboids().size(), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(statLabel(catMode, "volume")).append(Component.translatable("quarters.command.quarters.here.stats.volume.value", NamedTextColor.GRAY, Argument.string("volume", Integer.toString(quarter.getVolume()))));
        builder.appendNewline();
        builder.append(statLabel(catMode, "particle_size")).append(Component.text(quarter.getParticleSize() != null ? quarter.getParticleSize() : ConfigManager.getDefaultParticleSize(), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(statLabel(catMode, "creator")).append(ConfigManager.getFormattedName(quarter.getCreator(), Component.translatable("quarters.common.none", NamedTextColor.GRAY)));
        builder.appendNewline();
        builder.append(statLabel(catMode, "registered")).append(getFormattedDate(quarter.getRegistered()));
        builder.appendNewline();
        builder.append(statLabel(catMode, "claimed_at")).append(getFormattedDate(quarter.getClaimedAt()));

        return builder.build();
    }

    private Component getTrustedComponent(Quarter quarter, boolean catMode) {
        List<Resident> trusted = quarter.getTrustedResidents();
        if (trusted.isEmpty()) return Component.translatable(catKey(catMode, "trusted.none"), NamedTextColor.GRAY);

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
        builder.append(permLabel(ActionType.BUILD)).append(Component.text(permissions.createPermissionLine(ActionType.BUILD), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(permLabel(ActionType.DESTROY)).append(Component.text(permissions.createPermissionLine(ActionType.DESTROY), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(permLabel(ActionType.SWITCH)).append(Component.text(permissions.createPermissionLine(ActionType.SWITCH), NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(permLabel(ActionType.ITEM_USE)).append(Component.text(permissions.createPermissionLine(ActionType.ITEM_USE), NamedTextColor.GRAY));

        return builder.build();
    }

    private Component getFormattedDate(Long timestamp) {
        if (timestamp == null) return Component.translatable("quarters.common.not_applicable", NamedTextColor.GRAY);

        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return Component.text(formatter.format(date), NamedTextColor.GRAY);
    }

    private String labelKey(boolean catMode, @NotNull String name) {
        return catKey(catMode, "label." + name);
    }

    private String catKey(boolean catMode, @NotNull String name) {
        return "quarters.command.quarters.here." + (catMode ? "cat." : "") + name;
    }

    private Component statLabel(boolean catMode, @NotNull String name) {
        return Component.translatable(catKey(catMode, "stats." + name), NamedTextColor.DARK_GRAY).append(Component.text(": ", NamedTextColor.DARK_GRAY));
    }

    private Component permLabel(@NotNull ActionType actionType) {
        return Component.text(actionType.getCommonName() + ": ", NamedTextColor.DARK_GRAY);
    }
}
