/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.PitbullyPlugin;
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class DelWarpCommand
/*    */   implements CommandExecutor {
/*    */   private JavaPlugin plugin;
/*    */   
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
/* 18 */     if (!(commandSender instanceof Player)) {
/* 19 */       return true;
/*    */     }
/* 21 */     Player player = (Player)commandSender;
/* 22 */     UUID uuid = player.getUniqueId();
/* 23 */     execute(player, args);
/* 24 */     this.plugin = (JavaPlugin)PitbullyPlugin.getInstance();
/* 25 */     this.plugin.saveConfig();
/*    */     
/* 27 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   private void execute(Player player, String[] args) {
/* 32 */     if (args.length != 1) {
/* 33 */       player.sendMessage("zu wenig oder zu viele argumente");
/*    */       return;
/*    */     } 
/* 36 */     String warp = args[0];
/* 37 */     if (!Locations.checkWarpLocation(warp)) {
/* 38 */       player.sendMessage("Der Warp " + warp + " existiert nicht :(");
/*    */       return;
/*    */     } 
/* 41 */     player.sendMessage("Der Warp " + warp + " wurde gelöscht! :)");
/* 42 */     Locations.deleteWarpLocation(warp);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\DelWarpCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */