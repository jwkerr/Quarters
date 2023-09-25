package net.earthmc.quarters.task;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SelectionParticleTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getInventory().getItemInMainHand().getType() != Quarters.wand)
                continue;

            Selection selection = SelectionManager.selectionMap.get(player);

            if (selection == null)
                continue;

            Location pos1 = selection.getPos1();
            Location pos2 = selection.getPos2();

            if (pos1 != null && pos2 != null) {
                int x1 = pos1.getBlockX();
                int y1 = pos1.getBlockY();
                int z1 = pos1.getBlockZ();
                int x2 = pos2.getBlockX();
                int y2 = pos2.getBlockY();
                int z2 = pos2.getBlockZ();

                int length = Math.abs(x1 -x2);
                int height = Math.abs(y1 - y2);
                int width = Math.abs(z1 - z2);

                int volume = length * height * width;

                if (volume > Quarters.instance.getConfig().getInt("max_volume"))
                    continue;

                List<int[]> edges = new ArrayList<>();
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    edges.add(new int[]{x, y1, z1});
                    edges.add(new int[]{x, y2, z1});
                    edges.add(new int[]{x, y1, z2});
                    edges.add(new int[]{x, y2, z2});
                }

                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    edges.add(new int[]{x1, y, z1});
                    edges.add(new int[]{x2, y, z1});
                    edges.add(new int[]{x1, y, z2});
                    edges.add(new int[]{x2, y, z2});
                }

                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    edges.add(new int[]{x1, y1, z});
                    edges.add(new int[]{x2, y1, z});
                    edges.add(new int[]{x1, y2, z});
                    edges.add(new int[]{x2, y2, z});
                }

                for (int[] coordinate : edges) {
                    player.spawnParticle(Particle.SCRAPE, coordinate[0] + 0.5, coordinate[1] + 0.5, coordinate[2] + 0.5, 1, 0, 0, 0, 0);
                }
            }
        }
    }
}
