package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BackCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        if (Locations.checkLastLocation(((Player) commandSender).getUniqueId())) {
            ((Player) commandSender).teleport(Objects.requireNonNull(Locations.getLastLocation(((Player) commandSender).getUniqueId())));
        } else {
            commandSender.sendMessage("§cEs gibt keinen weg zurück!");
        }

        return false;
    }
}
