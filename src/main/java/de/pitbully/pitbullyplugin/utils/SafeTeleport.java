package de.pitbully.pitbullyplugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SafeTeleport {

//TODO fix this method
    /**
     * Teleports a player to a location.
     * This method will attempt to teleport the player to the specified location.
     * If the location is not safe, the method will attempt to find a safe location nearby.
     * If a safe location is found, the player will be teleported to that location.
     *
     * @param player The player to teleport.
     * @param loc    The location to teleport the player to.
     * @return true if the player was successfully teleported, false otherwise.
     */
    public static boolean teleport(Player player, Location loc) {
        Location tempLoc = loc.clone();
        while (tempLoc.getBlockY() <= 255) {
            if (tempLoc.getBlock().isEmpty() && tempLoc.clone().add(0, -1, 0).getBlock().isEmpty()) {
                player.teleport(new Location(tempLoc.getWorld(), tempLoc.getX(), tempLoc.getY()-1, tempLoc.getZ(), loc.getYaw(), loc.getPitch()));
                return true;
            }
            tempLoc.add(0, 1, 0);
        }
        return false;
    }
}
