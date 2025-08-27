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
        
        if (!player.hasPermission("pitbullyplugin.back")) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage("§cFehler: Zu viele Argumente!");
            player.sendMessage("§eVerwendung: /back [death|teleport]");
            return true;
        }

        // Determine which type of back teleport to perform
        String backType = args.length > 0 ? args[0].toLowerCase() : "last";
        
        switch (backType) {
            case "death":
                return handleDeathBack(player);
                
            case "teleport":
                return handleTeleportBack(player);
                
            default:
                return handleLastLocationBack(player);
        }
    }
    
    /**
     * Handles the death back command (/back death).
     * 
     * @param player The player executing the command
     * @return true if the command was handled successfully
     */
    private boolean handleDeathBack(Player player) {
        if (!player.hasPermission("pitbullyplugin.back.death")) {
            player.sendMessage("§cDu hast keine Berechtigung, um zu deinem Todesort zurückzukehren!");
            return true;
        }
        
        if (!LocationManager.checkLastDeathLocation(player.getUniqueId())) {
            player.sendMessage("§cDu hast keinen gespeicherten Todesort!");
            player.sendMessage("§eStirb, um einen Todesort zu haben, zu dem du zurückkehren kannst.");
            return true;
        }
        
        Location deathLocation = LocationManager.getLastDeathLocation(player.getUniqueId());
        if (deathLocation == null) {
            player.sendMessage("§cFehler beim Laden des Todesortes!");
            return true;
        }
        
        teleport(player, deathLocation, "§aZum Todesort zurück teleportiert! :)");
        return true;
    }
    
    /**
     * Handles the teleport back command (/back teleport).
     * 
     * @param player The player executing the command
     * @return true if the command was handled successfully
     */
    private boolean handleTeleportBack(Player player) {
        if (!player.hasPermission("pitbullyplugin.back.teleport")) {
            player.sendMessage("§cDu hast keine Berechtigung, um zu deinem letzten Teleportationsort zurückzukehren!");
            return true;
        }
        
        if (!LocationManager.checkLastTeleportLocation(player.getUniqueId())) {
            player.sendMessage("§cDu hast keinen gespeicherten Teleportationsort!");
            player.sendMessage("§eTeleportiere dich, um einen Ort zu haben, zu dem du zurückkehren kannst.");
            return true;
        }
        
        Location teleportLocation = LocationManager.getLastTeleportLocation(player.getUniqueId());
        if (teleportLocation == null) {
            player.sendMessage("§cFehler beim Laden des Teleportationsortes!");
            return true;
        }
        
        teleport(player, teleportLocation, "§aZum letzten Teleportationsort zurück teleportiert! :)");
        return true;
    }
    
    /**
     * Handles the general back command (/back without arguments).
     * 
     * @param player The player executing the command
     * @return true if the command was handled successfully
     */
    private boolean handleLastLocationBack(Player player) {
        if (!LocationManager.checkLastLocation(player.getUniqueId())) {
            player.sendMessage("§cEs gibt keinen Weg zurück!");
            player.sendMessage("§eTeleportiere dich oder stirb, um einen Rückweg zu haben.");
            return true;
        }
        
        Location lastLocation = LocationManager.getLastLocation(player.getUniqueId());
        if (lastLocation == null) {
            player.sendMessage("§cFehler beim Laden der letzten Position!");
            return true;
        }
        
        teleport(player, lastLocation, "§aZurück teleportiert! :)");
        return true;
    }
    
    /**
     * Teleports the player to the specified location with a custom success message.
     * 
     * @param player The player to teleport
     * @param location The location to teleport to
     * @param successMessage The message to send on successful teleportation
     */
    private void teleport(Player player, Location location, String successMessage) {
        if (SafeTeleport.teleport(player, location)) {
            player.sendMessage(successMessage);
        } else {
            player.sendMessage("§cEs gab ein Problem beim Teleportieren. Versuche es erneut!");
        }
    }
}
