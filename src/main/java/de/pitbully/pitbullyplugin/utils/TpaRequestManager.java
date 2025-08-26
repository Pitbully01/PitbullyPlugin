package de.pitbully.pitbullyplugin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Verwalter für TPA-Anfragen. Trennt ausgehende (pro Anfragenden) und eingehende
 * (pro Zielspieler), sodass jeder Spieler maximal eine offene Anfrage hat.
 */
public class TpaRequestManager {

    // Ausgehende Anfragen, indexiert nach Anfragenden (requesterUUID -> request)
    private static final Map<UUID, TpaRequest> outgoingByRequester = new HashMap<>();
    // Eingehende Anfragen, indexiert nach Ziel (targetUUID -> request)
    private static final Map<UUID, TpaRequest> incomingByTarget = new HashMap<>();

    public static void add(TpaRequest request) {
        outgoingByRequester.put(request.getRequester().getUniqueId(), request);
        incomingByTarget.put(request.getTarget().getUniqueId(), request);
    }

    public static void removeByRequester(UUID requesterId) {
        TpaRequest removed = outgoingByRequester.remove(requesterId);
        if (removed != null) {
            incomingByTarget.remove(removed.getTarget().getUniqueId());
        }
    }

    public static TpaRequest getOutgoing(UUID requesterId) {
        return outgoingByRequester.get(requesterId);
    }

    public static TpaRequest getIncoming(UUID targetId) {
        return incomingByTarget.get(targetId);
    }

    public static boolean hasOutgoing(UUID requesterId) {
        return outgoingByRequester.containsKey(requesterId);
    }

    public static boolean hasIncoming(UUID targetId) {
        return incomingByTarget.containsKey(targetId);
    }

    /**
     * Entfernt jegliche Anfragen, die an den Spieler gebunden sind (z. B. bei Quit).
     */
    public static void clearFor(Player player) {
        UUID id = player.getUniqueId();
        removeByRequester(id);
        // Falls der Spieler Ziel einer Anfrage ist
        TpaRequest incoming = incomingByTarget.remove(id);
        if (incoming != null) {
            outgoingByRequester.remove(incoming.getRequester().getUniqueId());
        }
    }
}
