package de.pitbully.pitbullyplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /enderchest and /ec commands.
 * Allows players to access their own or other players' enderchests.
 * 
 * <p>This command provides two functionalities:
 * <ul>
 * <li>Without arguments: Opens the player's own enderchest</li>
 * <li>With player name: Opens another player's enderchest (requires permission)</li>
 * </ul>
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class EnderchestCommand implements CommandExecutor {
  
  /**
   * Executes the enderchest command.
   * 
   * @param commandSender The command sender
   * @param command The command that was executed
   * @param s The alias of the command which was used
   * @param args The arguments passed to the command (optional player name)
   * @return true if the command was handled successfully
   */
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
      return true;
    }
    
    Player player = (Player)commandSender;
    
    if (args.length > 1) {
      player.sendMessage("§cFehler: Zu viele Argumente!");
      player.sendMessage("§eVerwendung: /enderchest [spielername]");
      return true;
    }
    
    // Öffne eigene Enderchest
    if (args.length == 0) {
      if (!player.hasPermission("pitbullyplugin.enderchest")) {
        player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
        return true;
      }
      
      player.openInventory(player.getEnderChest());
      player.sendMessage("§aEnderchest geöffnet!");
      return true;
    }
    
    // Öffne Enderchest eines anderen Spielers
    if (args.length == 1) {
      if (!player.hasPermission("pitbullyplugin.enderchest.others")) {
        player.sendMessage("§cDu hast keine Berechtigung, die Enderchest anderer Spieler zu öffnen!");
        return true;
      }
      
      Player target = commandSender.getServer().getPlayer(args[0]);
      if (target == null) {
        player.sendMessage("§cSpieler '" + args[0] + "' wurde nicht gefunden!");
        return true;
      }
      
      player.openInventory(target.getEnderChest());
      player.sendMessage("§aEnderchest von " + target.getName() + " geöffnet!");
      return true;
    }
    
    return true;
  }
}
