package de.pitbully.pitbullyplugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Utility class for safe teleportation of players.
 * Ensures players are teleported to safe locations without suffocating.
 */
public class SafeTeleport {
    
    private static final int MAX_WORLD_HEIGHT = 255;
    
    /**
     * Safely teleports a player to the specified location.
     * Searches for a safe spot above the target location if necessary.
     * 
     * @param player The player to teleport
     * @param location The target location
     * @return true if teleportation was successful, false otherwise
     */
    public static boolean teleport(Player player, Location location) {
        if (player == null || location == null || location.getWorld() == null) {
            return false;
        }
        
        Location safeLoc = findSafeLocation(location);
        if (safeLoc != null) {
            player.teleport(safeLoc);
            return true;
        }
        return false;
    }
    
    /**
     * Finds a safe location starting from the given location.
     * 
     * @param location The starting location
     * @return A safe location or null if none found
     */
    private static Location findSafeLocation(Location location) {
        Location tempLoc = location.clone();
        
        while (tempLoc.getBlockY() <= MAX_WORLD_HEIGHT) {
            if (isSafeLocation(tempLoc)) {
                return new Location(
                    tempLoc.getWorld(),
                    tempLoc.getX(),
                    tempLoc.getY() - 1.0,
                    tempLoc.getZ(),
                    location.getYaw(),
                    location.getPitch()
                );
            }
            tempLoc.add(0.0, 1.0, 0.0);
        }
        return null;
    }
    
    /**
     * Checks if a location is safe for teleportation.
     * 
     * @param location The location to check
     * @return true if the location is safe, false otherwise
     */
    private static boolean isSafeLocation(Location location) {
        return location.getBlock().isEmpty() && 
               location.clone().add(0.0, -1.0, 0.0).getBlock().isEmpty();
    }
}
