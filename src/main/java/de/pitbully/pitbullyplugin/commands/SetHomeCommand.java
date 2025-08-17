package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SetHomeCommand implements CommandExecutor {
  private JavaPlugin plugin;
  
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) {
      return true;
    }
    Player player = (Player)commandSender;

    
    Locations.updateHomeLocation(player.getUniqueId(), player.getLocation());
    player.sendMessage("Home gesetzt! :)");
    this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
    this.plugin.saveConfig();
    
    return false;
  }
}
