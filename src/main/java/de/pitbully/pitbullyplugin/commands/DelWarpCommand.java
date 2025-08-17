package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DelWarpCommand
  implements CommandExecutor {
  private JavaPlugin plugin;
  
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!(commandSender instanceof Player)) {
      return true;
    }
    Player player = (Player)commandSender;
    execute(player, args);
    this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
    this.plugin.saveConfig();
    
    return false;
  }

  
  private void execute(Player player, String[] args) {
    if (args.length != 1) {
      player.sendMessage("zu wenig oder zu viele argumente");
      return;
    } 
    String warp = args[0];
    if (!Locations.checkWarpLocation(warp)) {
      player.sendMessage("Der Warp " + warp + " existiert nicht :(");
      return;
    } 
    player.sendMessage("Der Warp " + warp + " wurde gelöscht! :)");
    Locations.deleteWarpLocation(warp);
  }
}
