/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import de.pitbully.pitbullyplugin.utils.SafeTeleport;
/*    */ import java.util.Objects;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class HomeCommand
/*    */   implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
/* 17 */     if (!(commandSender instanceof Player)) {
/* 18 */       return true;
/*    */     }
/* 20 */     Player player = (Player)commandSender;
/* 21 */     if (Locations.checkHomeLocation(player.getUniqueId())) {
/* 22 */       if (SafeTeleport.teleport(player, Objects.<Location>requireNonNull(Locations.getHomeLocation(player.getUniqueId())))) {
/* 23 */         player.sendMessage("Du wurdest zurück nach ♥Hause♥ teleportiert! :)");
/*    */       } else {
/* 25 */         player.sendMessage("§cEs gab ein Problem beim teleportieren");
/*    */       } 
/*    */     } else {
/* 28 */       player.sendMessage("§cKein Home gesetzt");
/*    */     } 
/*    */     
/* 31 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\HomeCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */