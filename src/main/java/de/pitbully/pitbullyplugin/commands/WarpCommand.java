package de.pitbully.pitbullyplugin.commands;


import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WarpCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
            Player player = (Player) commandSender;
            execute(player, strings);
            return false;
    }

    private void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("zu wenig oder zu viele argumente");
            return;
        }
        String warp = args[0];
        if (Locations.checkWarpLocation(warp)) {
            player.teleport(Locations.getWarpLocation(warp));
            player.sendMessage("Woosch, du wurdest zu " + warp + "teleportiert :)");
        } else {
            player.sendMessage("§cDieser Warp existiert nicht");
        }
    }
}