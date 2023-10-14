package net.earthmc.quarters.listener;

import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.object.QuartersPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerConnectionListener implements Listener {
    public static List<QuartersPlayer> quartersPlayerList = new ArrayList<>();

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        QuartersPlayer quartersPlayer = QuartersAPI.getInstance().getQuartersPlayer(player);

        if (!(quartersPlayerList.contains(quartersPlayer)))
            quartersPlayerList.add(quartersPlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        QuartersPlayer quartersPlayer = QuartersAPI.getInstance().getQuartersPlayer(player);

        quartersPlayerList.remove(quartersPlayer);
    }
}
