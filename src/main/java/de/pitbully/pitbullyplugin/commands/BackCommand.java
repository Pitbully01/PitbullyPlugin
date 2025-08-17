/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import java.util.Objects;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class BackCommand
/*    */   implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
/* 17 */     if (!(commandSender instanceof Player)) return true;
/*    */     
/* 19 */     if (Locations.checkLastLocation(((Player)commandSender).getUniqueId())) {
/* 20 */       ((Player)commandSender).teleport(Objects.<Location>requireNonNull(Locations.getLastLocation(((Player)commandSender).getUniqueId())));
/*    */     } else {
/* 22 */       commandSender.sendMessage("§cEs gibt keinen weg zurück!");
/*    */     } 
/*    */     
/* 25 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\BackCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */