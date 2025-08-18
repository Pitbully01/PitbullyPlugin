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
 * Command executor for the /sethome command.
 * Allows players to set their home location to their current position.
 * 
 * <p>This command saves the player's current location as their home,
 * which can later be accessed using the /home command.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class SetHomeCommand implements CommandExecutor {
  private JavaPlugin plugin;
  
  /**
   * Executes the sethome command.
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
      player.sendMessage("§eVerwendung: /sethome");
      return true;
    }
    
    if (!player.hasPermission("pitbullyplugin.sethome")) {
      player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
      return true;
    }
    
    Locations.updateHomeLocation(player.getUniqueId(), player.getLocation());
    player.sendMessage("§aHome erfolgreich gesetzt! :)");
    
    this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
    this.plugin.saveConfig();
    
    return true;
  }
}
