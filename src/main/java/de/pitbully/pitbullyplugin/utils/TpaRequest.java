package de.pitbully.pitbullyplugin.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.pitbully.pitbullyplugin.PitbullyPlugin;

/**
 * Repräsentiert eine Teleport-Anfrage ("/tpa") zwischen zwei Spielern.
 * <p>
 * Diese Klasse kümmert sich um das Senden der Anfrage, die Ablaufbenachrichtigung
 * und die Ausführung bei Annahme oder Ablehnung. Die Lebenszeitverwaltung (z. B.
 * Speichern/Aufheben aktiver Anfragen) sollte außerhalb erfolgen.
 */
public class TpaRequest {

    /** Der anfragende Spieler (wird zum Ziel teleportiert). */
    private final Player requester;
    /** Der Zielspieler (Empfänger der Anfrage). */
    private final Player target;
    /** Timeout in Sekunden, bis die Anfrage automatisch verfällt (aus config.yml). */
    private final int tpRequestTimeout;
    /** Geplante Aufgabe für den Timeout, damit sie bei Annahme/Ablehnung abgebrochen werden kann. */
    private BukkitTask timeoutTask;

    /**
     * Erstellt eine neue Teleport-Anfrage.
     *
     * Annahmen:
     * - Beide Spieler sind zum Zeitpunkt der Erstellung online.
     * - Gültigkeits- und Duplikatprüfung (z. B. bestehende Anfrage) erfolgt außerhalb.
     */
    public TpaRequest(Player requester, Player target) {
        this.requester = requester;
        this.target = target;
        // Timeout aus Konfiguration laden, Fallback: 30 Sekunden
        ConfigManager cm = PitbullyPlugin.getInstance() != null ? PitbullyPlugin.getInstance().getConfigManager() : null;
        this.tpRequestTimeout = (cm != null) ? cm.getTpaRequestTimeout() : 30;
    }

    /**
     * Sendet die Anfrage an den Zielspieler und plant eine Ablaufmeldung nach Timeout.
     */
    public void sendRequest() {
        // Prüfen: Hat der Anfragende schon eine offene ausgehende Anfrage?
        if (TpaRequestManager.hasOutgoing(requester.getUniqueId())) {
            requester.sendMessage("§cDu hast bereits eine aktive Teleport-Anfrage.");
            return;
        }

        // Prüfen: Hat der Zielspieler bereits eine offene eingehende Anfrage?
        if (TpaRequestManager.hasIncoming(target.getUniqueId())) {
            requester.sendMessage("§cDer Spieler " + target.getName() + " hat bereits eine offene Teleport-Anfrage.");
            return;
        }

        // Anfrage registrieren
        TpaRequestManager.add(this);

        // Hinweis an den Zielspieler mit kurzer Anleitung
        target.sendMessage("§a" + requester.getName() +
                " möchte sich zu dir teleportieren. Akzeptiere innerhalb " + tpRequestTimeout +
                " Sekunden mit /tpaccept oder lehne ab mit /tpdeny");

        // Ablaufbenachrichtigung nach Ablauf des Timeouts
        timeoutTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Nur benachrichtigen, wenn der Zielspieler noch online ist.
                if (target.isOnline()) {
                    target.sendMessage("§cDein Teleport-Antrag von " + requester.getName() + " ist abgelaufen.");
                }
                if (requester.isOnline()) {
                    requester.sendMessage("§cDeine Teleport-Anfrage an " + target.getName() + " ist abgelaufen.");
                }
                // Aufräumen: Anfrage entfernen und Handle zurücksetzen
                TpaRequestManager.removeByRequester(requester.getUniqueId());
                timeoutTask = null;
            }
        }.runTaskLater(PitbullyPlugin.getInstance(), tpRequestTimeout * 20L); // Sekunden -> Ticks
    }

    /**
     * Akzeptiert die Anfrage und teleportiert den Anfragenden zum Zielspieler.
     */
    public void acceptRequest() {
        // Sicherer Teleport (z. B. Höhenermittlung / Kollisionen) via SafeTeleport
        if(!target.isOnline()) {
            requester.sendMessage("§cDer Zielspieler ist nicht mehr online.");
            return;
        }
        if (SafeTeleport.teleport(requester, target.getLocation())) {
            requester.sendMessage("§aDu wurdest zu " + target.getName() + " teleportiert.");
        } else {
            requester.sendMessage("§cTeleportation fehlgeschlagen.");
        }
    if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    TpaRequestManager.removeByRequester(requester.getUniqueId());
    }

    /**
     * Lehnt die Anfrage ab und informiert den Anfragenden.
     */
    public void denyRequest() {
        requester.sendMessage("§cDein Teleport-Antrag zu " + target.getName() + " wurde abgelehnt.");
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
        TpaRequestManager.removeByRequester(requester.getUniqueId());
    }

    // Getter für Manager/Logik
    public Player getRequester() { return requester; }
    public Player getTarget() { return target; }

}
