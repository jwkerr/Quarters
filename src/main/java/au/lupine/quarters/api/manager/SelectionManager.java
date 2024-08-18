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

    public void selectPosition(Player player, Location location, SelectionType type) {
        CuboidSelection selection = CUBOID_SELECTION_MAP.computeIfAbsent(player, s -> new CuboidSelection());
        switch (type) {
            case LEFT -> selection.setCornerOne(location);
            case RIGHT -> selection.setCornerTwo(location);
        }
    }

    public CuboidSelection getSelection(Player player) {
        return CUBOID_SELECTION_MAP.computeIfAbsent(player, s -> new CuboidSelection());
    }

    public void clearSelection(Player player) {
        CUBOID_SELECTION_MAP.remove(player);
    }

    public void setCuboids(@NotNull Player player, @NotNull List<Cuboid> cuboids) {
        CUBOIDS_MAP.put(player, cuboids);
    }

    public List<Cuboid> getCuboids(Player player) {
        return CUBOIDS_MAP.getOrDefault(player, new ArrayList<>());
    }

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

    public boolean isLocationInWorldBounds(@NotNull Location location) {
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
