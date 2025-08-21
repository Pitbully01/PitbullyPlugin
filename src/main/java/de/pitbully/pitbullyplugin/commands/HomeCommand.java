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
 * Command executor for the /home command.
 * Teleports players to their set home location.
 */
public class HomeCommand implements CommandExecutor {
    
    
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
            player.sendMessage("§eVerwendung: /home");
            return true;
        }
        
        if (!player.hasPermission("pitbullyplugin.home")) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }
        
        if (!LocationManager.checkHomeLocation(player.getUniqueId())) {
            player.sendMessage("§cKein Home gesetzt!");
            player.sendMessage("§eVerwende /sethome um dein Home zu setzen.");
            return true;
        }
        
        Location homeLocation = LocationManager.getHomeLocation(player.getUniqueId());
        if (homeLocation == null) {
            player.sendMessage("§cKein Home gesetzt!");
            player.sendMessage("§eVerwende /sethome um dein Home zu setzen.");
            return true;
        }
        
        if (SafeTeleport.teleport(player, homeLocation)) {
            player.sendMessage("§aDu wurdest zurück nach ♥Hause♥ teleportiert! :)");
        } else {
            player.sendMessage("§cEs gab ein Problem beim Teleportieren. Versuche es erneut!");
        }
        
        return true;
    }
}
