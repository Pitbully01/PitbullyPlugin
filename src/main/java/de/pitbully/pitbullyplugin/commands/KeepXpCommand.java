package de.pitbully.pitbullyplugin.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.storage.LocationStorage;
import de.pitbully.pitbullyplugin.utils.PlayerData;

public class KeepXpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }
        Player player = (Player) commandSender;

        if (!player.hasPermission("pitbullyplugin.keepxp")) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        if (args.length == 0) {
            // Show current status
            boolean isKeeping = isKeepingXp(player);
            player.sendMessage("§eAktuell wird deine XP beim Sterben " + (isKeeping ? "" : "nicht ") + "behalten.");
            return true;
        }

        if (args.length > 2) {
            player.sendMessage("§cFehler: Zu viele Argumente!");
            player.sendMessage("§eVerwendung: /keepxp [on|off] [player]");
            return true;
        }

        String action = args[0].toLowerCase();
        Player targetPlayer = player;
        
        if (args.length > 1) {
            if (!player.hasPermission("pitbullyplugin.keepxp.others")) {
                player.sendMessage("§cDu hast keine Berechtigung, KeepXP für andere Spieler zu ändern!");
                return true;
            }
            targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                player.sendMessage("§cSpieler '" + args[1] + "' ist nicht online!");
                return true;
            }
        }

        switch (action) {
            case "on":
                return handleKeepXpChange(player, targetPlayer, true);

            case "off":
                return handleKeepXpChange(player, targetPlayer, false);

            default:
                player.sendMessage("§cUnbekannte Aktion: " + action);
                player.sendMessage("§eVerwendung: /keepxp [on|off] [player]");
                return true;
        }
    }

    private boolean isKeepingXp(Player player) {
        return LocationStorage.isKeepingXp(player);
    }

    private boolean handleKeepXpChange(Player executor, Player target, boolean enable) {
        UUID targetId = target.getUniqueId();
        LocationStorage storage = LocationManager.getStorage();
        
        // Get or create player data
        PlayerData playerData = storage.getPlayerData(targetId);
        if (playerData == null) {
            playerData = new PlayerData();
        }
        
        // Set keepXp status directly
        playerData.setKeepXp(enable);
        storage.savePlayerData(targetId, playerData);
        
        String status = enable ? "aktiviert" : "deaktiviert";
        String color = enable ? "§a" : "§c";
        
        if (executor.equals(target)) {
            executor.sendMessage(color + "§lKeep XP " + status + "! Du wirst " + (enable ? "" : "nicht ") + "deine XP beim Sterben behalten.");
        } else {
            executor.sendMessage(color + "§lKeep XP für " + target.getName() + " " + status + "!");
            target.sendMessage(color + "§lDein Keep XP wurde " + status + "!");
        }
        
        return true;
    }
}
