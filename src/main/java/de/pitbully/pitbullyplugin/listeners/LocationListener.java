/*    */ package de.pitbully.pitbullyplugin.listeners;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.PlayerDeathEvent;
/*    */ import org.bukkit.event.player.PlayerTeleportEvent;
/*    */ 
/*    */ public class LocationListener
/*    */   implements Listener
/*    */ {
/*    */   @EventHandler
/*    */   public void onPlayerDeath(PlayerDeathEvent event) {
/* 15 */     UUID playerId = event.getEntity().getUniqueId();
/* 16 */     Locations.updateLastDeathLocations(playerId, event.getEntity().getLocation());
/*    */   }
/*    */   @EventHandler
/*    */   public void onPlayerTeleport(PlayerTeleportEvent event) {
/*    */     UUID playerId;
/* 21 */     PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();
/* 22 */     switch (teleportCause) { case COMMAND: case PLUGIN:
/*    */       case SPECTATE:
/* 24 */         playerId = event.getPlayer().getUniqueId();
/* 25 */         Locations.updateLastTeleportLocations(playerId, event.getPlayer().getLocation());
/*    */         break; }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\listeners\LocationListener.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */