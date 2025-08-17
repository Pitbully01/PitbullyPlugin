/*     */ package de.pitbully.pitbullyplugin;
/*     */ 
/*     */ import de.pitbully.pitbullyplugin.commands.BackCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.DelHomeCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.DelWarpCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.EnderchestCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.HomeCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.SetHomeCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.SetWarpCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.TabCompleters.WarpTabCompleter;
/*     */ import de.pitbully.pitbullyplugin.commands.WarpCommand;
/*     */ import de.pitbully.pitbullyplugin.commands.WorkbenchCommand;
/*     */ import de.pitbully.pitbullyplugin.listeners.LocationListener;
/*     */ import de.pitbully.pitbullyplugin.listeners.PlayerDeathListener;
/*     */ import de.pitbully.pitbullyplugin.utils.Locations;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.command.TabCompleter;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PitbullyPlugin
/*     */   extends JavaPlugin
/*     */ {
/*     */   private static PitbullyPlugin instance;
/*     */   private File configFile;
/*     */   private FileConfiguration config;
/*     */   
/*     */   public void onEnable() {
/*  38 */     registerCommands();
/*  39 */     registerEvents();
/*  40 */     instance = this;
/*     */ 
/*     */     
/*  43 */     initConfig();
/*  44 */     loadConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initConfig() {
/*  53 */     if (this.config == null) {
/*  54 */       this.configFile = new File(getDataFolder(), "config.yml");
/*  55 */       this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PitbullyPlugin getInstance() {
/*  66 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  76 */     saveConfig();
/*     */   }
/*     */   
/*     */   private void registerCommands() {
/*  80 */     registerCommand("home", (CommandExecutor)new HomeCommand());
/*  81 */     registerCommand("sethome", (CommandExecutor)new SetHomeCommand());
/*  82 */     registerCommand("delhome", (CommandExecutor)new DelHomeCommand());
/*  83 */     registerCommand("back", (CommandExecutor)new BackCommand());
/*  84 */     registerCommand("setwarp", (CommandExecutor)new SetWarpCommand());
/*  85 */     registerCommand("warp", (CommandExecutor)new WarpCommand(), (TabCompleter)new WarpTabCompleter());
/*  86 */     registerCommand("delwarp", (CommandExecutor)new DelWarpCommand(), (TabCompleter)new WarpTabCompleter());
/*  87 */     registerCommand("enderchest", (CommandExecutor)new EnderchestCommand());
/*  88 */     registerCommand("workbench", (CommandExecutor)new WorkbenchCommand());
/*     */   }
/*     */ 
/*     */   
/*     */   private void registerEvents() {
/*  93 */     getServer().getPluginManager().registerEvents((Listener)new LocationListener(), (Plugin)this);
/*  94 */     getServer().getPluginManager().registerEvents((Listener)new PlayerDeathListener(), (Plugin)this);
/*     */   }
/*     */   
/*     */   private void registerCommand(String command, CommandExecutor commandExecutor) {
/*  98 */     ((PluginCommand)Objects.<PluginCommand>requireNonNull(getCommand(command))).setExecutor(commandExecutor);
/*     */   }
/*     */   
/*     */   private void registerCommand(String command, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
/* 102 */     ((PluginCommand)Objects.<PluginCommand>requireNonNull(getCommand(command))).setTabCompleter(tabCompleter);
/* 103 */     registerCommand(command, commandExecutor);
/*     */   }
/*     */   
/*     */   private void loadConfig() {
/* 107 */     if (this.config == null) {
/* 108 */       getLogger().warning("Config not initialized. Skipping load.");
/*     */       
/*     */       return;
/*     */     } 
/* 112 */     if (!this.configFile.exists() || this.configFile.length() == 0L) {
/* 113 */       getLogger().warning("Config.yml not found or empty. Using default values.");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 118 */     Locations.loadFromConfig(this.config);
/*     */   }
/*     */   
/*     */   public void saveConfig() {
/* 122 */     if (this.config == null) {
/* 123 */       getLogger().warning("Config not initialized. Skipping save.");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 128 */     Locations.saveToConfig(this.config);
/*     */     
/*     */     try {
/* 131 */       this.config.save(this.configFile);
/* 132 */     } catch (IOException e) {
/* 133 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugin\PitbullyPlugin.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */