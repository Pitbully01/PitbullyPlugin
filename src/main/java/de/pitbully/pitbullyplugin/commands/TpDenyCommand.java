package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.utils.TpaRequest;
import de.pitbully.pitbullyplugin.utils.TpaRequestManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Denies a pending teleport request for the executing player (as target).
 */
public class TpDenyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("pitbullyplugin.tpdeny")) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        TpaRequest request = TpaRequestManager.getIncoming(player.getUniqueId());
        if (request == null) {
            player.sendMessage("§cDu hast keine offene Teleport-Anfrage.");
            return true;
        }

        request.denyRequest();
        player.sendMessage("§eTeleport-Anfrage abgelehnt.");
        return true;
    }
}
