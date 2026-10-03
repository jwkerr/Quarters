package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.SelectionRenderer;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.render.PacketEventsSelectionRenderer;
import au.lupine.quarters.object.state.FlagType;
import au.lupine.quarters.object.wrapper.CuboidSelection;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SelectionRendererManager {

    private static SelectionRendererManager instance;

    private final SelectionRenderer renderer = new PacketEventsSelectionRenderer();
    private final Map<UUID, String> renderKeys = new ConcurrentHashMap<>();

    private SelectionRendererManager() {}

    public static SelectionRendererManager getInstance() {
        if (instance == null) instance = new SelectionRendererManager();
        return instance;
    }

    public void render(@NotNull Player player) {
        boolean renderNormalOutlines = QuarterManager.getInstance().shouldRenderOutlinesForPlayer(player);
        boolean renderForcedPvpBoundaries = shouldRenderForcedPvpBoundaries(player);

        if (!renderNormalOutlines && !renderForcedPvpBoundaries) {
            hide(player);
            return;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        boolean glow = resident != null && ResidentMetadataManager.getInstance().hasSelectionGlow(resident);
        String renderKey = getRenderKey(player, glow, renderNormalOutlines, renderForcedPvpBoundaries);
        if (renderKey.equals(renderKeys.get(player.getUniqueId()))) return;

        hide(player);
        renderKeys.put(player.getUniqueId(), renderKey);
        if (renderNormalOutlines) renderCurrentSelection(player, glow);
        renderQuarters(player, glow, renderNormalOutlines, renderForcedPvpBoundaries);
    }

    public void hide(@NotNull Player player) {
        renderKeys.remove(player.getUniqueId());
        renderer.hide(player);
    }

    private @NotNull String getRenderKey(@NotNull Player player, boolean glow, boolean renderNormalOutlines, boolean renderForcedPvpBoundaries) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getWorld().getUID())
                .append(':').append(glow)
                .append(':').append(renderNormalOutlines)
                .append(':').append(renderForcedPvpBoundaries);

        SelectionManager selectionManager = SelectionManager.getInstance();
        CuboidSelection selection = selectionManager.getSelection(player);
        if (renderNormalOutlines) appendCuboids(builder, QuartersMessaging.PLUGIN_COLOUR, selectionManager.getCuboids(player));

        Cuboid currentSelection = selection.getCuboid();
        if (renderNormalOutlines && currentSelection != null) appendCuboid(builder, QuartersMessaging.PLUGIN_COLOUR, currentSelection);

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return builder.toString();

        for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
            if (!renderNormalOutlines && !shouldForceRenderPvpQuarter(quarter)) continue;

            builder.append(":quarter=").append(quarter.getUUID());
            builder.append(',').append(shouldGlowQuarter(quarter, glow));
            appendCuboids(builder, quarter.getDisplayColour(), quarter.getCuboids());
        }

        return builder.toString();
    }

    private void appendCuboids(@NotNull StringBuilder builder, @NotNull Color colour, @NotNull List<Cuboid> cuboids) {
        for (Cuboid cuboid : cuboids) {
            appendCuboid(builder, colour, cuboid);
        }
    }

    private void appendCuboid(@NotNull StringBuilder builder, @NotNull Color colour, @NotNull Cuboid cuboid) {
        builder.append(":cuboid=")
                .append(cuboid.getWorld().getUID())
                .append(',').append(cuboid.getMinX())
                .append(',').append(cuboid.getMinY())
                .append(',').append(cuboid.getMinZ())
                .append(',').append(cuboid.getMaxX())
                .append(',').append(cuboid.getMaxY())
                .append(',').append(cuboid.getMaxZ())
                .append(',').append(colour.getRGB());
    }

    private void renderCurrentSelection(@NotNull Player player, boolean glow) {
        SelectionManager selectionManager = SelectionManager.getInstance();
        CuboidSelection selection = selectionManager.getSelection(player);
        List<Cuboid> cuboids = new ArrayList<>(selectionManager.getCuboids(player));

        Cuboid currentSelection = selection.getCuboid();
        if (currentSelection != null) cuboids.add(currentSelection);

        if (!cuboids.isEmpty()) renderer.show(player, cuboids, QuartersMessaging.PLUGIN_COLOUR, glow);
    }

    private void renderQuarters(@NotNull Player player, boolean glow, boolean renderNormalOutlines, boolean renderForcedPvpBoundaries) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return;

        for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
            if (!renderNormalOutlines && (!renderForcedPvpBoundaries || !shouldForceRenderPvpQuarter(quarter))) continue;

            renderer.show(player, quarter.getCuboids(), quarter.getDisplayColour(), shouldGlowQuarter(quarter, glow));
        }
    }

    private boolean shouldRenderForcedPvpBoundaries(@NotNull Player player) {
        if (!Quarters.getInstance().config().renderer.enabled) return false;
        if (!Quarters.getInstance().config().quarters.pvpSettings.visibleBoundary) return false;

        return TownyAPI.getInstance().getTown(player.getLocation()) != null;
    }

    private boolean shouldForceRenderPvpQuarter(@NotNull Quarter quarter) {
        return quarter.hasFlag(FlagType.PVP);
    }

    private boolean shouldGlowQuarter(@NotNull Quarter quarter, boolean playerGlow) {
        if (playerGlow) return true;

        ConfigManager config = Quarters.getInstance().config();
        return config.quarters.pvpSettings.visibleGlow && quarter.hasFlag(FlagType.PVP);
    }
}
