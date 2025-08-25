package de.pitbully.pitbullyplugin.utils;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Utility class for safe teleportation of players.
 * Ensures players are teleported to safe locations without suffocating.
 * 
 * <p>This utility automatically finds safe locations near the target destination,
 * preventing players from being teleported into blocks or falling into the void.
 * It supports modern Minecraft world heights (-64 to 319).
 * 
 * <p>Safety checks can be configured in the config.yml file under settings.teleport.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class SafeTeleport {
    
    /** Maximum world height for modern Minecraft versions (1.18+) */
    private static final int MAX_WORLD_HEIGHT = 319;
    
    /**
     * Safely teleports a player to the specified location.
     * Searches for a safe spot near the target location if necessary.
     * 
     * <p>This method first checks if safety checks are enabled in config.
     * If disabled, performs direct teleportation. If enabled, searches for
     * a safe location near the target. If a safe location is found, the 
     * player is teleported there.
     * 
     * @param player The player to teleport (must not be null)
     * @param location The target location (must not be null with valid world)
     * @return true if teleportation was successful, false otherwise
     */
    public static boolean teleport(Player player, Location location) {
        if (player == null || location == null || location.getWorld() == null) {
            return false;
        }
        
        // Get config manager (may be null during initialization)
        PitbullyPlugin plugin = PitbullyPlugin.getInstance();
        ConfigManager configManager = plugin != null ? plugin.getConfigManager() : null;
        
        // If config manager is not available or safety checks are disabled, teleport directly
        if (configManager == null || !configManager.isSafetyCheckEnabled()) {
            if (configManager != null) {
                configManager.debug("Safety checks disabled, teleporting directly to: " + locationToString(location));
            }
            return player.teleport(location);
        }
        
        // Find safe location with configured max distance
        Location safeLoc = findSafeLocation(location, configManager.getMaxSafeDistance());
        if (safeLoc != null) {
            configManager.debug("Safe teleport to: " + locationToString(safeLoc));
            return player.teleport(safeLoc);
        }
        
        configManager.debug("No safe location found near: " + locationToString(location));
        return false;
    }

    /**
     * Finds a safe location starting from the given location.
     * First tries the original location, then searches upward and downward.
     * 
     * <p>A safe location is defined as having:
     * <ul>
     * <li>Clear space for the player's feet and head</li>
     * <li>Solid ground beneath the player</li>
     * <li>No risk of falling into the void</li>
     * </ul>
     * 
     * @param location The starting location to search from
     * @param maxDistance The maximum distance to search in blocks
     * @return A safe location or null if none found within world bounds
     */
    private static Location findSafeLocation(Location location, int maxDistance) {
        // First, try the original location
        if (isSafeLocation(location)) {
            return location.clone();
        }
        
        // Search upward with max distance limit
        Location tempLoc = location.clone();
        int originalY = tempLoc.getBlockY();
        int maxY = Math.min(originalY + maxDistance, MAX_WORLD_HEIGHT);
        
        for (int y = originalY; y <= maxY; y++) {
            tempLoc.setY(y);
            if (isSafeLocation(tempLoc)) {
                return tempLoc.clone();
            }
        }
        
        // Search downward from original location with max distance limit
        tempLoc = location.clone();
        int minY = Math.max(originalY - maxDistance, -64);
        
        for (int y = originalY; y >= minY; y--) {
            tempLoc.setY(y);
            if (isSafeLocation(tempLoc)) {
                return tempLoc.clone();
            }
        }
        
        return null;
    }
    
    /**
     * Converts a location to a readable string format.
     * 
     * @param location The location to convert
     * @return String representation of the location
     */
    private static String locationToString(Location location) {
        if (location == null || location.getWorld() == null) {
            return "null";
        }
        return String.format("%s: %.1f, %.1f, %.1f", 
                           location.getWorld().getName(),
                           location.getX(),
                           location.getY(),
                           location.getZ());
    }
    
    /**
     * Checks if a location is safe for teleportation.
     * A location is safe if the player has room to stand and won't fall into the void.
     * 
     * <p>Safety criteria:
     * <ul>
     * <li>Feet position must be passable (air, water, etc.)</li>
     * <li>Head position must be passable</li>
     * <li>Ground below must be solid and not passable</li>
     * <li>World must be valid</li>
     * </ul>
     * 
     * @param location The location to check for safety
     * @return true if the location is safe for teleportation, false otherwise
     */
    private static boolean isSafeLocation(Location location) {
        if (location.getWorld() == null) {
            return false;
        }
        
        Location feetLocation = location.clone();
        Location headLocation = feetLocation.clone().add(0, 1, 0);
        Location groundLocation = feetLocation.clone().add(0, -1, 0);
        
        // Check if feet and head positions are clear (air or passable)
        boolean feetClear = feetLocation.getBlock().isPassable();
        boolean headClear = headLocation.getBlock().isPassable();
        
        // Check if there's solid ground below (not air, not liquid)
        boolean hasGround = !groundLocation.getBlock().isPassable() && 
                           groundLocation.getBlock().getType().isSolid();
        
        return feetClear && headClear && hasGround;
    }
}
