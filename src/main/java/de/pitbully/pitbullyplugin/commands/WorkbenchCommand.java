package de.pitbully.pitbullyplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /workbench and /wb commands.
 * Allows players to open a crafting table interface without placing one.
 * 
 * <p>This command provides convenient access to crafting functionality
 * without requiring players to carry or place a workbench block.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class WorkbenchCommand implements CommandExecutor {
  
  /**
   * Executes the workbench command.
   * 
   * @param commandSender The command sender
   * @param command The command that was executed
   * @param s The alias of the command which was used
   * @param args The arguments passed to the command
   * @return true if the command was handled successfully
   */
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
      return true;
    }
    Player player = (Player)commandSender;
    
    if (args.length != 0) {
      player.sendMessage("§cFehler: Dieser Befehl benötigt keine Argumente!");
      player.sendMessage("§eVerwendung: /workbench");
      return true;
    }
    
    if (!player.hasPermission("pitbullyplugin.workbench")) {
      player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
      return true;
    }
    
    player.openWorkbench(null, true);
    
    return true;
  }
}

