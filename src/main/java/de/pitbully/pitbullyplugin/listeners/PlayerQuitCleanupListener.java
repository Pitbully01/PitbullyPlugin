package de.pitbully.pitbullyplugin.listeners;

import de.pitbully.pitbullyplugin.utils.TpaRequestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Cleans up any pending TPA requests when a player quits.
 */
public class PlayerQuitCleanupListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Remove any outgoing/incoming requests related to this player
        TpaRequestManager.clearFor(event.getPlayer());
    }
}
