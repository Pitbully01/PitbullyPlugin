package de.pitbully.pitbullyplugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SafeTeleport {


    public static boolean teleport(Player player, Location loc) {
        while (loc.getBlockY() <= 255) {
            if (loc.getBlock().isEmpty() && loc.clone().add(0, -1, 0).getBlock().isEmpty()) {
                loc.add(0, 1, 0);
                player.teleport(loc);
                return true;
            }
            loc.add(0, 1, 0);
        }
        return false;
    }
}
