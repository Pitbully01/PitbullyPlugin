/*    */ package de.pitbully.pitbullyplugin.commands;
/*    */ 
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class WorkbenchCommand
/*    */   implements CommandExecutor {
/*    */   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
/* 12 */     if (!(commandSender instanceof Player)) {
/* 13 */       return true;
/*    */     }
/* 15 */     Player player = (Player)commandSender;
/* 16 */     execute(player);
/* 17 */     return false;
/*    */   }
/*    */   private void execute(Player player) {
/* 20 */     player.openWorkbench(null, true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\WorkbenchCommand.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */