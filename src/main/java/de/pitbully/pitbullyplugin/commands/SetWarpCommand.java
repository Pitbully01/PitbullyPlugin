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
 * Command executor for the /setwarp command.
 * Allows administrators to create server warp points.
 * 
 * <p>This command creates a new warp location that all players can use
 * with the /warp command. This is typically an admin-only operation.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class SetWarpCommand implements CommandExecutor {
  private JavaPlugin plugin;
  
  /**
   * Executes the setwarp command.
   * 
   * @param commandSender The command sender
   * @param command The command that was executed
   * @param s The alias of the command which was used
   * @param args The arguments passed to the command (expects warp name)
   * @return true if the command was handled successfully
   */
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
      return true;
    }
    Player player = (Player)commandSender;
    
    if (args.length != 1) {
      player.sendMessage("§cFehler: Bitte gib einen Warp-Namen an!");
      player.sendMessage("§eVerwendung: /setwarp <warp-name>");
      return true;
    }
    
    if (!player.hasPermission("pitbullyplugin.setwarp")) {
      player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
      return true;
    }
    
    String warp = args[0];
    if (LocationManager.checkWarpLocation(warp)) {
      player.sendMessage("§cDer Warp " + warp + " existiert bereits!");
      player.sendMessage("§eVerwende /delwarp " + warp + " um ihn zu löschen und neu zu setzen.");
      return true;
    }
    
    LocationManager.updateWarpLocation(warp, player.getLocation());
    player.sendMessage("§aDer Warp " + warp + " wurde erfolgreich gesetzt! :)");
    
    this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
    this.plugin.saveConfig();
    
    return true;
  }
}
