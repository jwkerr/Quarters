package au.lupine.quarters.api.manager;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.SelectionRenderer;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.render.PacketEventsSelectionRenderer;
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
        if (!QuarterManager.getInstance().shouldRenderOutlinesForPlayer(player)) {
            hide(player);
            return;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) {
            hide(player);
            return;
        }

        boolean glow = ResidentMetadataManager.getInstance().hasSelectionGlow(resident);
        String renderKey = getRenderKey(player, glow);
        if (renderKey.equals(renderKeys.get(player.getUniqueId()))) return;

        hide(player);
        renderKeys.put(player.getUniqueId(), renderKey);
        renderCurrentSelection(player, glow);
        renderQuarters(player, glow);
    }

    public void hide(@NotNull Player player) {
        renderKeys.remove(player.getUniqueId());
        renderer.hide(player);
    }

    private @NotNull String getRenderKey(@NotNull Player player, boolean glow) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getWorld().getUID())
                .append(':').append(glow);

        SelectionManager selectionManager = SelectionManager.getInstance();
        CuboidSelection selection = selectionManager.getSelection(player);
        appendCuboids(builder, QuartersMessaging.PLUGIN_COLOUR, selectionManager.getCuboids(player));

        Cuboid currentSelection = selection.getCuboid();
        if (currentSelection != null) appendCuboid(builder, QuartersMessaging.PLUGIN_COLOUR, currentSelection);

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return builder.toString();

        for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
            builder.append(":quarter=").append(quarter.getUUID());
            appendCuboids(builder, quarter.getColour(), quarter.getCuboids());
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

    private void renderQuarters(@NotNull Player player, boolean glow) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) return;

        for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
            renderer.show(player, quarter.getCuboids(), quarter.getColour(), glow);
        }
    }
}
