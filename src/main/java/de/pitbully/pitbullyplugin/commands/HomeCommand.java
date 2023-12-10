package de.pitbully.pitbullyplugin.commands;


import java.util.Objects;
import java.util.UUID;

import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        } else {
            Player player = (Player) commandSender;
            UUID uuid = player.getUniqueId();

            if (Locations.checkHomeLocation(player.getUniqueId())) {
                player.teleport(Objects.requireNonNull(Locations.getHomeLocation(player.getUniqueId())));
                player.sendMessage("Du wurdest zurück nach ♥Hause♥ teleportiert! :)");
            } else {
                player.sendMessage("§cKein Home gesetzt");
            }

            return false;
        }
    }
}