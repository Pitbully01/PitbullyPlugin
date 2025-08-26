package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.utils.TpaRequest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Führt den /tpahere Befehl aus.
 * <p>
 * Erwartet genau ein Argument (Zielspieler) und bittet diesen, sich zu dir zu teleportieren.
 * Die eigentliche Behandlung (Timeout, Annahme/Ablehnung) erfolgt in {@link TpaRequest}.
 */
public class TpaHereCommand implements CommandExecutor {

    /**
     * Handhabt den Aufruf von /tpahere.
     *
     * Vertragsbedingungen:
     * - Nur Spieler dürfen den Befehl ausführen.
     * - Es muss genau ein Argument (Spielername) angegeben werden.
     * - Zielspieler darf nicht der Ausführende selbst sein und muss online sein.
     *
     * @return immer true, da der Befehl intern vollständig verarbeitet wird
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        // Sicherstellen, dass der Befehl von einem Spieler kommt (nicht von Konsole/Command-Block)
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }

        Player player = (Player) commandSender;

        // Berechtigungsprüfung
    // Eigene Permission für tpahere (separat von /tpa)
    if (!player.hasPermission("pitbullyplugin.tpahere")) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        // Syntaxprüfung
        if (args.length != 1) {
            player.sendMessage("§cFehler: Dieser Befehl benötigt genau ein Argument!");
            player.sendMessage("§eVerwendung: /tpahere <Spieler>");
            return true;
        }

        // Selbst-Teleportation verhindern (Name-Vergleich; Case-Sensitivität je nach Server-Einstellung)
    if (args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage("§cFehler: Du kannst dich nicht zu dir selbst teleportieren!");
            return true;
        }

        // Zielspieler auflösen; getPlayer liefert null, wenn der Spieler nicht online ist
        Player targetPlayer = player.getServer().getPlayer(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage("§cFehler: Spieler '" + args[0] + "' ist nicht online oder wurde nicht gefunden!");
            return true;
        }

        // Anfrage erzeugen und an den Zielspieler senden
        new TpaRequest(targetPlayer, player).sendRequest();
        player.sendMessage("§aTeleport-Anfrage an §e" + targetPlayer.getName() + " §agesendet.");

        return true;
    }
}

        
 