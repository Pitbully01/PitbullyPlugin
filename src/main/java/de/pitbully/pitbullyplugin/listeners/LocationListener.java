package de.pitbully.pitbullyplugin.listeners;

import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class LocationListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID playerId = event.getEntity().getUniqueId();
        Locations.updateLastDeathLocations(playerId, event.getEntity().getLocation());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();
        switch (teleportCause) {
            case COMMAND, PLUGIN, SPECTATE -> {
                UUID playerId = event.getPlayer().getUniqueId();
                Locations.updateLastTeleportLocations(playerId, event.getPlayer().getLocation());
            }
        }
    }
}
