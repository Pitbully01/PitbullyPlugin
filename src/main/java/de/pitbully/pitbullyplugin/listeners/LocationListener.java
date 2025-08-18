package de.pitbully.pitbullyplugin.listeners;

import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Listener for player teleportation events.
 * Tracks player teleportation for the /back command functionality.
 * 
 * <p>This listener monitors player teleportation events and saves the
 * location before teleportation, allowing players to use the /back command
 * to return to their previous position.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class LocationListener implements Listener {
    
    /**
     * Handles player teleportation events.
     * Saves the player's location before teleportation for /back functionality.
     * 
     * <p>Only tracks teleportation from:
     * <ul>
     * <li>Commands (/tp, /warp, etc.)</li>
     * <li>Plugin teleportation</li>
     * <li>Spectator mode teleportation</li>
     * </ul>
     * 
     * @param event The player teleport event
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();
        
        // Only track teleportation from commands, plugins, or spectating
        // Ignore natural teleportation like enderpearls, chorus fruit, etc.
        switch (teleportCause) {
            case COMMAND:
            case PLUGIN:
            case SPECTATE:
                // Save the location before teleportation for /back command
                Locations.updateLastTeleportLocations(
                    event.getPlayer().getUniqueId(), 
                    event.getFrom()
                );
                break;
            default:
                // Ignore other teleport causes
                break;
        }
    }
}
