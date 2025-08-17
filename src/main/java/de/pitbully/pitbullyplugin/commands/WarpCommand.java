/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import de.pitbully.pitbullyplugin.utils.SafeTeleport;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WarpCommand
/*    */   implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
/* 17 */     if (!(commandSender instanceof Player)) {
/* 18 */       return true;
/*    */     }
/* 20 */     Player player = (Player)commandSender;
/* 21 */     execute(player, strings);
/* 22 */     return false;
/*    */   }
/*    */   
/*    */   private void execute(Player player, String[] args) {
/* 26 */     if (args.length != 1) {
/* 27 */       player.sendMessage("zu wenig oder zu viele argumente");
/*    */       return;
/*    */     } 
/* 30 */     String warp = args[0];
/* 31 */     if (Locations.checkWarpLocation(warp)) {
/* 32 */       if (SafeTeleport.teleport(player, Locations.getWarpLocation(warp))) {
/* 33 */         player.sendMessage("Woosch, du wurdest zu " + warp + " teleportiert :)");
/*    */       } else {
/* 35 */         player.sendMessage("§cEs gab ein Problem beim teleportieren");
/*    */       } 
/*    */     } else {
/* 38 */       player.sendMessage("§cDieser Warp existiert nicht");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\WarpCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */