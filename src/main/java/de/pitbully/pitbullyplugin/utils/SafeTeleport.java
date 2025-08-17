/*    */ package de.pitbully.pitbullyplugin.utils;
/*    */ 
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SafeTeleport
/*    */ {
/*    */   public static boolean teleport(Player player, Location loc) {
/* 20 */     Location tempLoc = loc.clone();
/* 21 */     while (tempLoc.getBlockY() <= 255) {
/* 22 */       if (tempLoc.getBlock().isEmpty() && tempLoc.clone().add(0.0D, -1.0D, 0.0D).getBlock().isEmpty()) {
/* 23 */         player.teleport(new Location(tempLoc.getWorld(), tempLoc.getX(), tempLoc.getY() - 1.0D, tempLoc.getZ(), loc.getYaw(), loc.getPitch()));
/* 24 */         return true;
/*    */       } 
/* 26 */       tempLoc.add(0.0D, 1.0D, 0.0D);
/*    */     } 
/* 28 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugi\\utils\SafeTeleport.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */