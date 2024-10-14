package de.pitbully.pitbullyplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnderchestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
            if(strings.length == 1){
                Player target = commandSender.getServer().getPlayer(strings[0]);
                if(target == null){
                    commandSender.sendMessage("§cSpieler nicht gefunden!");
                    return true;
                }
                execute((Player) commandSender, target);
                return false;
            }
            Player player = (Player) commandSender;
            execute(player);
            return false;
    }
    private void execute(Player player){
        player.openInventory(player.getEnderChest());
    }
    private void execute(Player player, Player target){
        player.openInventory(target.getEnderChest());
    }
}
