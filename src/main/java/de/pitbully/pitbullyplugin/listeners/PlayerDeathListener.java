package de.pitbully.pitbullyplugin.listeners;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listener for player death events.
 * Handles experience keeping and location tracking for the /back command.
 * 
 * <p>This listener provides two main functions:
 * <ul>
 * <li>Saves death locations for /back command functionality</li>
 * <li>Preserves player experience if they have the appropriate permission</li>
 * </ul>
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class PlayerDeathListener implements Listener {
    
    /**
     * Handles player death events.
     * Saves the death location and optionally preserves experience.
     * 
     * <p>This method always saves the death location for /back functionality.
     * If the player has the 'pitbullyplugin.keepxp' permission, their
     * experience will be preserved upon death.
     * 
     * @param event The player death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Save death location for /back command
        LocationManager.updateLastDeathLocations(player.getUniqueId(), player.getLocation());
        
        // Handle experience keeping if player has permission
        if (player.hasPermission("pitbullyplugin.keepxp")) {
            int exp = player.getTotalExperience();
            event.setKeepLevel(true);
            event.setDroppedExp(0);
            player.setTotalExperience(exp);
            
        }
    }
}
