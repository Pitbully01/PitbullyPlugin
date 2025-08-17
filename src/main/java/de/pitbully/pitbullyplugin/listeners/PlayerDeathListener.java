/*    */ package de.pitbully.pitbullyplugin.listeners;
/*    */ 
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.PlayerDeathEvent;
/*    */ 
/*    */ public class PlayerDeathListener
/*    */   implements Listener {
/*    */   @EventHandler
/*    */   public void onPlayerDeath(PlayerDeathEvent event) {
/* 12 */     Player player = event.getEntity();
/* 13 */     if (player.hasPermission("pitbullyplugin.keepxp")) {
/* 14 */       int exp = player.getTotalExperience();
/* 15 */       event.setKeepLevel(true);
/* 16 */       event.setDroppedExp(0);
/* 17 */       player.setTotalExperience(exp);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\listeners\PlayerDeathListener.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */