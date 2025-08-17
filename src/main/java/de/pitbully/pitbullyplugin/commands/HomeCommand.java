package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.utils.Locations;
import de.pitbully.pitbullyplugin.utils.SafeTeleport;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /home command.
 * Teleports players to their set home location.
 */
public class HomeCommand implements CommandExecutor {
    
    private static final String HOME_SUCCESS_MESSAGE = "Du wurdest zurück nach ♥Hause♥ teleportiert! :)";
    private static final String TELEPORT_ERROR_MESSAGE = "§cEs gab ein Problem beim teleportieren";
    private static final String NO_HOME_MESSAGE = "§cKein Home gesetzt";
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, 
                           @NotNull Command command, 
                           @NotNull String label, 
                           @NotNull String[] args) {
        
        if (!(commandSender instanceof Player)) {
            return true;
        }
        
        Player player = (Player) commandSender;
        
        if (!Locations.checkHomeLocation(player.getUniqueId())) {
            player.sendMessage(NO_HOME_MESSAGE);
            return true;
        }
        
        Location homeLocation = Locations.getHomeLocation(player.getUniqueId());
        if (homeLocation == null) {
            player.sendMessage(NO_HOME_MESSAGE);
            return true;
        }
        
        if (SafeTeleport.teleport(player, homeLocation)) {
            player.sendMessage(HOME_SUCCESS_MESSAGE);
        } else {
            player.sendMessage(TELEPORT_ERROR_MESSAGE);
        }
        
        return true;
    }
}