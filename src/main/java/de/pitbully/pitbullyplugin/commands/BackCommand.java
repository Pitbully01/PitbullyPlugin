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
 * Command executor for the /back command.
 * Teleports players to their last recorded location (death or teleport location).
 */
public class BackCommand implements CommandExecutor {
    
    private static final String NO_LOCATION_MESSAGE = "§cEs gibt keinen Weg zurück!";
    private static final String TELEPORT_ERROR_MESSAGE = "§cEs gab ein Problem beim teleportieren";
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, 
                           @NotNull Command command, 
                           @NotNull String label, 
                           @NotNull String[] args) {
        
        if (!(commandSender instanceof Player)) {
            return true;
        }
        
        Player player = (Player) commandSender;
        
        if (!Locations.checkLastLocation(player.getUniqueId())) {
            player.sendMessage(NO_LOCATION_MESSAGE);
            return true;
        }
        
        Location lastLocation = Locations.getLastLocation(player.getUniqueId());
        if (lastLocation == null) {
            player.sendMessage(NO_LOCATION_MESSAGE);
            return true;
        }
        
        if (!SafeTeleport.teleport(player, lastLocation)) {
            player.sendMessage(TELEPORT_ERROR_MESSAGE);
        }
        
        return true;
    }
}
