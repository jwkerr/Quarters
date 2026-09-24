package au.lupine.quarters.command.quarters.method.find;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.Pair;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ForsaleMethod extends CommandMethod {

    private static final int QUARTERS_PER_PAGE = 10;

    public ForsaleMethod() {
        super("forsale", "quarters.command.quarters.find.forsale");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("town", StringArgumentType.word())
                        .suggests((context, builder) -> suggestTownNames(builder))
                        .executes(context -> run(
                                context.getSource(),
                                context.getArgument("town", String.class),
                                1
                        ))
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(context -> run(
                                        context.getSource(),
                                        context.getArgument("town", String.class),
                                        IntegerArgumentType.getInteger(context, "page")
                                ))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack) {
        execute(stack, null, 1);
    }

    private int run(@NotNull CommandSourceStack stack, @Nullable String townName, int page) {
        try {
            execute(stack, townName, page);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(stack.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    private void execute(@NotNull CommandSourceStack stack, @Nullable String townName, int page) {
        Player player = getSenderAsPlayerOrThrow(stack);

        Town town = getTown(player, townName);
        if (town == null) return;

        List<Quarter> forSaleQuarters = QuarterManager.getInstance()
                .getQuarters(town)
                .stream()
                .filter(Quarter::isForSale)
                .sorted(Comparator.comparingDouble(Quarter::getPrice))
                .toList();

        if (forSaleQuarters.isEmpty()) {
            QuartersMessaging.sendErrorMessage(player, "quarters.command.quarter.find.forsale.no_quarters", Argument.string("town", town.getName()));
            return;
        }

        int pageCount = (int) Math.ceil((double) forSaleQuarters.size() / QUARTERS_PER_PAGE);
        if (page > pageCount) {
            QuartersMessaging.sendErrorMessage(
                    player,
                    "quarters.command.quarter.find.forsale.exceeded_pages",
                    Argument.numeric("current_page", page),
                    Argument.numeric("max_page", pageCount)
            );
            return;
        }

        int fromIndex = (page - 1) * QUARTERS_PER_PAGE;
        int toIndex = Math.min(fromIndex + QUARTERS_PER_PAGE, forSaleQuarters.size());
        List<Quarter> pageQuarters = forSaleQuarters.subList(fromIndex, toIndex);

        Component header = Component.translatable(
                        "quarters.command.quarter.find.forsale.title",
                        TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB()),
                        Argument.string("town", town.getName())
                )
                .appendSpace()
                .append(Component.translatable(
                        "quarters.command.quarter.find.forsale.count",
                        NamedTextColor.GRAY,
                        Argument.numeric("count", forSaleQuarters.size())
                ))
                .appendSpace()
                .append(Component.translatable(
                        "quarters.command.quarter.find.forsale.page",
                        NamedTextColor.DARK_GRAY,
                        Argument.numeric("current_page", page),
                        Argument.numeric("max_page", pageCount)
                ));

        List<Pair<String, Component>> labelled = new ArrayList<>();
        for (Quarter quarter : pageQuarters) {
            labelled.add(Pair.of(null, getQuarterComponent(quarter)));
        }

        Component navigation = getPageNavigation(town, page, pageCount);
        if (!navigation.equals(Component.empty())) labelled.add(Pair.of(null, navigation));

        QuartersMessaging.sendComponent(player, QuartersMessaging.getListComponent(header, labelled, null));
    }

    private @NotNull CompletableFuture<Suggestions> suggestTownNames(@NotNull SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();

        for (Town town : TownyAPI.getInstance().getTowns()) {
            String name = town.getName();
            if (name.toLowerCase().startsWith(remaining)) builder.suggest(name);
        }

        return builder.buildFuture();
    }

    private @Nullable Town getTown(@NotNull Player player, @Nullable String townName) {
        if (townName == null) {
            Town town = TownyAPI.getInstance().getTown(player.getLocation());

            if (town == null) {
                QuartersMessaging.sendErrorMessage(player, "quarters.command.quarter.find.forsale.not_standing_in_town");
            }

            return town;
        }

        Town town = TownyAPI.getInstance().getTown(townName);

        if (town == null) {
            QuartersMessaging.sendErrorMessage(
                    player,
                    "quarters.command.quarter.find.forsale.town_not_found",
                    Argument.string("name", townName)
            );
        }

        return town;
    }

    private @NotNull Component getQuarterComponent(@NotNull Quarter quarter) {
        Location location = quarter.getAnchor();
        if (location == null) location = quarter.getFirstCornerOfFirstCuboid();

        Component name = Component.text(quarter.getName(), TextColor.color(quarter.getColour().getRGB()))
                .hoverEvent(Component.translatable("quarters.command.quarter.find.forsale.quarter.hover", NamedTextColor.GRAY))
                .clickEvent(ClickEvent.runCommand("/quarters:q here " + quarter.getUUID()));

        Component price = getPriceComponent(quarter);
        Component locationComponent = QuartersMessaging.getLocationComponent(location)
                .hoverEvent(Component.translatable("quarters.command.quarter.find.forsale.location.hover", NamedTextColor.GRAY))
                .clickEvent(ClickEvent.copyToClipboard(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));

        return name
                .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                .append(price)
                .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                .append(locationComponent)
                .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    private @NotNull Component getPriceComponent(@NotNull Quarter quarter) {
        Double price = quarter.getPrice();
        if (price == null) return Component.empty();

        Component component = price == 0
                ? Component.translatable("quarters.command.quarters.here.price.free", NamedTextColor.GREEN)
                : Component.text(TownyEconomyHandler.getFormattedBalance(price), NamedTextColor.GREEN);

        return component
                .hoverEvent(Component.translatable("quarters.command.quarter.find.forsale.price.hover", NamedTextColor.GRAY))
                .clickEvent(ClickEvent.runCommand("/quarters:q claim " + quarter.getUUID()));
    }

    private @NotNull Component getPageNavigation(@NotNull Town town, int page, int pageCount) {
        Component navigation = Component.empty();

        if (page > 1) {
            navigation = navigation.append(
                    Component.translatable("quarters.command.quarter.find.forsale.previous", NamedTextColor.YELLOW)
                            .hoverEvent(Component.translatable("quarters.command.quarter.find.forsale.previous.hover", NamedTextColor.GRAY))
                            .clickEvent(ClickEvent.runCommand("/q find forsale " + town.getName() + " " + (page - 1)))
            );
        }

        if (page > 1 && page < pageCount) {
            navigation = navigation.append(Component.text(" | ", NamedTextColor.DARK_GRAY));
        }

        if (page < pageCount) {
            navigation = navigation.append(
                    Component.translatable("quarters.command.quarter.find.forsale.next", NamedTextColor.YELLOW)
                            .hoverEvent(Component.translatable("quarters.command.quarter.find.forsale.next.hover", NamedTextColor.GRAY))
                            .clickEvent(ClickEvent.runCommand("/q find forsale " + town.getName() + " " + (page + 1)))
            );
        }

        return navigation;
    }
}
