package de.pitbully.pitbullyplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            return true;
        } else {
            Player player = (Player) commandSender;
            if(player.hasPermission("PitbullyPlugin.feed")) {
                if (strings.length == 1 && player.hasPermission("PitbullyPlugin.feed.other")) {
                    Player target = commandSender.getServer().getPlayer(strings[0]);
                    if (target == null) {
                        commandSender.sendMessage("§cSpieler nicht gefunden!");
                        return true;
                    }
                    target.setFoodLevel(20);
                    player.sendMessage("§aDu hast " + target.getName() + " gefüttert!♥♥");
                    target.sendMessage("§aDu wurdest von "+ player.getName() +" gefüttert!♥♥");
                }
                player.setFoodLevel(20);
                player.sendMessage("§aDu wurdest gefüttert!♥♥");
            } else {
                player.sendMessage("§cDu hast keine Rechte dazu!");
            }
        }
        return false;
    }
}
