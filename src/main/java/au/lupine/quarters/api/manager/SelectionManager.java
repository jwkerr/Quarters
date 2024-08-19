package au.lupine.quarters.api.manager;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.state.SelectionType;
import au.lupine.quarters.object.wrapper.CuboidSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SelectionManager {

    private static SelectionManager instance;

    private static final Map<Player, CuboidSelection> CUBOID_SELECTION_MAP = new ConcurrentHashMap<>();
    private static final Map<Player, List<Cuboid>> CUBOIDS_MAP = new ConcurrentHashMap<>();

    private SelectionManager() {}

    public static SelectionManager getInstance() {
        if (instance == null) instance = new SelectionManager();
        return instance;
    }

    /**
     * @param player Player to select a position for
     * @param location Location to select
     * @param type {@link SelectionType} to use, left for pos one, right for pos two
     */
    public void selectPosition(@NotNull Player player, @NotNull Location location, @NotNull SelectionType type) {
        CuboidSelection selection = CUBOID_SELECTION_MAP.computeIfAbsent(player, s -> new CuboidSelection());
        switch (type) {
            case LEFT -> selection.setCornerOne(location);
            case RIGHT -> selection.setCornerTwo(location);
        }
    }

    /**
     * @return The player's current selection, will return an empty selection if they have not selected anything
     */
    public CuboidSelection getSelection(Player player) {
        return CUBOID_SELECTION_MAP.computeIfAbsent(player, s -> new CuboidSelection());
    }

    public void clearSelection(Player player) {
        CUBOID_SELECTION_MAP.remove(player);
    }

    public void setCuboids(@NotNull Player player, @NotNull List<Cuboid> cuboids) {
        CUBOIDS_MAP.put(player, cuboids);
    }

    /**
     * @return The player's currently added cuboids using /q selection add
     */
    public List<Cuboid> getCuboids(Player player) {
        return CUBOIDS_MAP.getOrDefault(player, new ArrayList<>());
    }

    /**
     * @return The player's currently added cuboids, if they have none it will try to add the result of their {@link CuboidSelection#getCuboid()} if it is not null
     */
    public List<Cuboid> getCuboidsOrSelectionAsCuboid(Player player) {
        List<Cuboid> cuboids = getCuboids(player);

        if (cuboids.isEmpty()) {
            CuboidSelection selection = getSelection(player);
            Cuboid cuboid = selection.getCuboid();

            if (cuboid != null) cuboids.add(cuboid);
        }

        return cuboids;
    }

    public void clearCuboids(Player player) {
        CUBOIDS_MAP.remove(player);
    }

    /**
     * @return True if the specified location is not lower than bedrock or higher than the world's height limit
     */
    public boolean isLocationInsideWorldBounds(@NotNull Location location) {
        World world = location.getWorld();
        double y = location.getY();

        return y < world.getMaxHeight() && y > world.getMinHeight();
    }

    public Component getSelectedPositionComponent(SelectionType type, Location location) {
        int pos = type.equals(SelectionType.LEFT) ? 1 : 2;

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Position " + pos + ": ", NamedTextColor.GRAY, TextDecoration.ITALIC));
        builder.append(QuartersMessaging.getLocationComponent(location));

        return builder.build();
    }
}
