package au.lupine.quarters.object.base;

import au.lupine.quarters.object.entity.Cuboid;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Collection;

public interface SelectionRenderer {
    void show(@NotNull Player player, @NotNull Collection<Cuboid> cuboids, @NotNull Color colour, boolean glow);

    void hide(@NotNull Player player);
}
