package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.utils.Locations;
import de.pitbully.pitbullyplugin.utils.SafeTeleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /warp command.
 * Allows players to teleport to server warp points.
 * 
 * <p>This command teleports players to predefined warp locations
 * that have been set by administrators using the /setwarp command.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class WarpCommand implements CommandExecutor {
  
  /**
   * Executes the warp command.
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
      player.sendMessage("§eVerwendung: /warp <warp-name>");
      return true;
    }
    
    if (!player.hasPermission("pitbullyplugin.warp")) {
      player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
      return true;
    }
    
    String warp = args[0];
    if (Locations.checkWarpLocation(warp)) {
      if (SafeTeleport.teleport(player, Locations.getWarpLocation(warp))) {
        player.sendMessage("§aWoosch, du wurdest zu " + warp + " teleportiert! :)");
      } else {
        player.sendMessage("§cEs gab ein Problem beim Teleportieren. Versuche es erneut!");
      } 
    } else {
      player.sendMessage("§cDieser Warp existiert nicht!");
      player.sendMessage("§eVerwende /warps um alle verfügbaren Warps zu sehen.");
    }
    
    return true;
  }
}
