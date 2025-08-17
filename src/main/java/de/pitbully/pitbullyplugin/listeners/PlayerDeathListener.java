package de.pitbully.pitbullyplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener
  implements Listener {
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (player.hasPermission("pitbullyplugin.keepxp")) {
      int exp = player.getTotalExperience();
      event.setKeepLevel(true);
      event.setDroppedExp(0);
      player.setTotalExperience(exp);
    } 
  }
}
