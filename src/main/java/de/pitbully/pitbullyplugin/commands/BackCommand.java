package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.storage.LocationManager;
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
 * 
 * <p>This command allows players to return to their previous location after:
 * <ul>
 * <li>Dying - returns to death location</li>
 * <li>Teleporting - returns to location before teleportation</li>
 * </ul>
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class BackCommand implements CommandExecutor {
    
    /**
     * Executes the back command.
     * 
     * @param commandSender The command sender
     * @param command The command that was executed
     * @param label The alias of the command which was used
     * @param args The arguments passed to the command
     * @return true if the command was handled successfully
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, 
                           @NotNull Command command, 
                           @NotNull String label, 
                           @NotNull String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }
        
        Player player = (Player) commandSender;
        
        if (args.length != 0) {
            player.sendMessage("§cFehler: Dieser Befehl benötigt keine Argumente!");
            player.sendMessage("§eVerwendung: /back");
            return true;
        }
        
        if (!player.hasPermission("pitbullyplugin.back")) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }
        
        if (!LocationManager.checkLastLocation(player.getUniqueId())) {
            player.sendMessage("§cEs gibt keinen Weg zurück!");
            player.sendMessage("§eTeleportiere dich oder stirb, um einen Rückweg zu haben.");
            return true;
        }
        
        Location lastLocation = LocationManager.getLastLocation(player.getUniqueId());
        if (lastLocation == null) {
            player.sendMessage("§cEs gibt keinen Weg zurück!");
            player.sendMessage("§eTeleportiere dich oder stirb, um einen Rückweg zu haben.");
            return true;
        }
        
        if (SafeTeleport.teleport(player, lastLocation)) {
            player.sendMessage("§aZurück teleportiert! :)");
        } else {
            player.sendMessage("§cEs gab ein Problem beim Teleportieren. Versuche es erneut!");
        }
        
        return true;
    }
}
