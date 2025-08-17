/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.PitbullyPlugin;
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class DelHomeCommand implements CommandExecutor {
/*    */   private JavaPlugin plugin;
/*    */   
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
/* 16 */     if (!(commandSender instanceof Player)) {
/* 17 */       return true;
/*    */     }
/* 19 */     Player player = (Player)commandSender;
/* 20 */     if (Locations.checkHomeLocation(player.getUniqueId())) {
/* 21 */       Locations.deleteHomeLocation(player.getUniqueId());
/* 22 */       player.sendMessage("Home gelöscht? :(");
/* 23 */       this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
/* 24 */       this.plugin.saveConfig();
/*    */     } else {
/* 26 */       player.sendMessage("§cKein Home Gesetzt? :(");
/*    */     } 
/* 28 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\DelHomeCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */