package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.storage.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /delhome command.
 * Allows players to delete their home location.
 * 
 * <p>This command removes the player's home location from the system,
 * requiring them to use /sethome again to set a new home.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class DelHomeCommand implements CommandExecutor {
  private JavaPlugin plugin;
  
  /**
   * Executes the delhome command.
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
      return true;
    }
    Player player = (Player)commandSender;
    
    if (args.length != 0) {
      player.sendMessage("§cFehler: Dieser Befehl benötigt keine Argumente!");
      player.sendMessage("§eVerwendung: /delhome");
      return true;
    }
    
    if (!player.hasPermission("pitbullyplugin.delhome")) {
      player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
      return true;
    }
    
    if (!LocationManager.checkHomeLocation(player.getUniqueId())) {
      player.sendMessage("§cKein Home gesetzt!");
      player.sendMessage("§eVerwende /sethome um ein Home zu setzen.");
      return true;
    }
    
    LocationManager.deleteHomeLocation(player.getUniqueId());
    player.sendMessage("§aDein Home wurde erfolgreich gelöscht!");
    
    this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
    this.plugin.saveConfig();
    
    return true;
  }
}
