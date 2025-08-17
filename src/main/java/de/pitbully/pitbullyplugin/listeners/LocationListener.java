package de.pitbully.pitbullyplugin.listeners;

import de.pitbully.pitbullyplugin.utils.Locations;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LocationListener
  implements Listener
{
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    UUID playerId = event.getEntity().getUniqueId();
    Locations.updateLastDeathLocations(playerId, event.getEntity().getLocation());
  }
  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    UUID playerId;
    PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();
    switch (teleportCause) { case COMMAND: case PLUGIN:
      case SPECTATE:
        playerId = event.getPlayer().getUniqueId();
        Locations.updateLastTeleportLocations(playerId, event.getPlayer().getLocation());
        break;
      default:
        break; }
  
  }
}
