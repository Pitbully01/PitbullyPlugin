package de.pitbully.pitbullyplugin.utils;

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
     * <p>This method first attempts to find a safe location near the target.
     * If a safe location is found, the player is teleported there.
     * 
     * @param player The player to teleport (must not be null)
     * @param location The target location (must not be null with valid world)
     * @return true if teleportation was successful, false otherwise
     */
    public static boolean teleport(Player player, Location location) {
        if (player == null || location == null || location.getWorld() == null) {
            return false;
        }
        
        Location safeLoc = findSafeLocation(location);
        if (safeLoc != null) {
            return player.teleport(safeLoc);
        }
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
     * @return A safe location or null if none found within world bounds
     */
    private static Location findSafeLocation(Location location) {
        // First, try the original location
        if (isSafeLocation(location)) {
            return location.clone();
        }
        
        // Search upward
        Location tempLoc = location.clone();
        for (int y = tempLoc.getBlockY(); y <= MAX_WORLD_HEIGHT; y++) {
            tempLoc.setY(y);
            if (isSafeLocation(tempLoc)) {
                return tempLoc.clone();
            }
        }
        
        // Search downward from original location
        tempLoc = location.clone();
        for (int y = tempLoc.getBlockY(); y >= -64; y--) {
            tempLoc.setY(y);
            if (isSafeLocation(tempLoc)) {
                return tempLoc.clone();
            }
        }
        
        return null;
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
