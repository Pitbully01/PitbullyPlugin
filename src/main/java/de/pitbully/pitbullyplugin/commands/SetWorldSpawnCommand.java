package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /setspawn command.
 * Allows administrators to set the world spawn location.
 * 
 * <p>This command sets the spawn point for the world to the player's
 * current location. New players and respawning players will spawn here.
 * This is an admin-only operation.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class SetWorldSpawnCommand implements CommandExecutor {
  private JavaPlugin plugin;
  
  /**
   * Executes the setspawn command.
   * 
   * @param commandSender The command sender
   * @param command The command that was executed
   * @param s The alias of the command which was used
   * @param args The arguments passed to the command
   * @return true if the command was handled successfully
   */
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
      return false;
    }
    Player player = (Player)commandSender;
    
    if (args.length != 0) {
      player.sendMessage("§cFehler: Dieser Befehl benötigt keine Argumente!");
      player.sendMessage("§eVerwendung: /setworldspawn");
      return true;
    }
    
    if (!player.hasPermission("pitbullyplugin.setworldspawn")) {
      player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
      return true;
    }
    
    Locations.updateWorldSpawnLocation(player.getLocation());
    player.sendMessage("§aWorldSpawn wurde erfolgreich auf deine aktuelle Position gesetzt!");
    
    this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
    this.plugin.saveConfig();
    
    return true;
  }
}
