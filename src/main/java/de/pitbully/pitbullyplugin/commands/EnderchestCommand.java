/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class EnderchestCommand
/*    */   implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
/* 13 */     if (!(commandSender instanceof Player)) {
/* 14 */       return true;
/*    */     }
/* 16 */     if (strings.length == 1) {
/* 17 */       Player target = commandSender.getServer().getPlayer(strings[0]);
/* 18 */       if (target == null) {
/* 19 */         commandSender.sendMessage("Player not found");
/* 20 */         return true;
/*    */       } 
/* 22 */       execute((Player)commandSender, target);
/* 23 */       return false;
/*    */     } 
/* 25 */     Player player = (Player)commandSender;
/* 26 */     execute(player);
/* 27 */     return false;
/*    */   }
/*    */   private void execute(Player player) {
/* 30 */     player.openInventory(player.getEnderChest());
/*    */   }
/*    */   private void execute(Player player, Player target) {
/* 33 */     player.openInventory(target.getEnderChest());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\EnderchestCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */