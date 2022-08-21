package cz.jesuschrist69.buildsystem.listeners;

import cz.jesuschrist69.buildsystem.component.BuildSystemListener;
import cz.jesuschrist69.buildsystem.manager.WorldManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@BuildSystemListener
public class WorldListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        World w = e.getPlayer().getWorld();
        if (w.getPlayers().size() == 0) WorldManager.EMPTY_WORLDS.put(w, System.currentTimeMillis());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        World w = e.getFrom();
        if (w.getPlayers().size() == 0) WorldManager.EMPTY_WORLDS.put(w, System.currentTimeMillis());
        WorldManager.EMPTY_WORLDS.remove(e.getPlayer().getWorld());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        World w = e.getFrom().getWorld();
        if (w.getPlayers().size() == 0) WorldManager.EMPTY_WORLDS.put(w, System.currentTimeMillis());
        WorldManager.EMPTY_WORLDS.remove(e.getTo().getWorld());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        WorldManager.EMPTY_WORLDS.remove(e.getPlayer().getWorld());
    }

}
