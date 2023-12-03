package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DeleteHomeCommand implements CommandExecutor {
    private JavaPlugin plugin;
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        } else {
            Player player = (Player) commandSender;
            if(Locations.checkHomeLocation(player.getUniqueId())) {
                Locations.deleteHomeLocation(player.getUniqueId());
                player.sendMessage("Home gelöscht? :(");
                plugin = PitbullyPlugin.getInstance();
                plugin.saveConfig();
            }else{
                player.sendMessage("§cKein Home Gesetzt? :(");
            }
            return false;
        }
    }
}