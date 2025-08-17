/*    */ package de.pitbully.pitbullyplugin.commands.TabCompleters;
/*    */ 
/*    */ import de.pitbully.pitbullyplugin.utils.Locations;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.command.TabCompleter;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class WarpTabCompleter
/*    */   implements TabCompleter
/*    */ {
/*    */   @Nullable
/*    */   public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
/* 17 */     List<String> completions = new ArrayList<>();
/* 18 */     if (command.getName().equalsIgnoreCase("warp") && args.length == 1) {
/* 19 */       for (String warpName : Locations.getWarpHashMap().keySet()) {
/* 20 */         if (warpName.toLowerCase().startsWith(args[0].toLowerCase())) {
/* 21 */           completions.add(warpName);
/*    */         }
/*    */       } 
/*    */     }
/* 25 */     return completions;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\commands\TabCompleters\WarpTabCompleter.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */