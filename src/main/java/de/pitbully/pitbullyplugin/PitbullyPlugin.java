package de.pitbully.pitbullyplugin;

import de.pitbully.pitbullyplugin.commands.BackCommand;
import de.pitbully.pitbullyplugin.commands.DelHomeCommand;
import de.pitbully.pitbullyplugin.commands.DelWarpCommand;
import de.pitbully.pitbullyplugin.commands.EnderchestCommand;
import de.pitbully.pitbullyplugin.commands.HomeCommand;
import de.pitbully.pitbullyplugin.commands.SetHomeCommand;
import de.pitbully.pitbullyplugin.commands.SetWarpCommand;
import de.pitbully.pitbullyplugin.commands.TabCompleters.WarpTabCompleter;
import de.pitbully.pitbullyplugin.commands.WarpCommand;
import de.pitbully.pitbullyplugin.commands.WorkbenchCommand;
import de.pitbully.pitbullyplugin.listeners.LocationListener;
import de.pitbully.pitbullyplugin.listeners.PlayerDeathListener;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Main plugin class for PitbullyPlugin.
 * Handles plugin initialization, command registration, and configuration management.
 */
public final class PitbullyPlugin extends JavaPlugin {
    
    private static PitbullyPlugin instance;
    private File configFile;
    private FileConfiguration config;
    
    @Override
    public void onEnable() {
        instance = this;
        
        initConfig();
        registerCommands();
        registerEvents();
        loadConfig();
        
        getLogger().info("PitbullyPlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("PitbullyPlugin has been disabled!");
    }
    
    /**
     * Get the plugin instance.
     * 
     * @return The plugin instance
     */
    public static PitbullyPlugin getInstance() {
        return instance;
    }
    
    /**
     * Initialize the configuration file.
     */
    private void initConfig() {
        if (this.config == null) {
            this.configFile = new File(getDataFolder(), "config.yml");
            this.config = YamlConfiguration.loadConfiguration(this.configFile);
        }
    }
    
    /**
     * Register all plugin commands.
     */
    private void registerCommands() {
        WarpTabCompleter warpTabCompleter = new WarpTabCompleter();
        
        registerCommand("home", new HomeCommand());
        registerCommand("sethome", new SetHomeCommand());
        registerCommand("delhome", new DelHomeCommand());
        registerCommand("back", new BackCommand());
        registerCommand("setwarp", new SetWarpCommand());
        registerCommand("warp", new WarpCommand(), warpTabCompleter);
        registerCommand("delwarp", new DelWarpCommand(), warpTabCompleter);
        registerCommand("enderchest", new EnderchestCommand());
        registerCommand("workbench", new WorkbenchCommand());
        
        // Register aliases manually to ensure they work
        registerCommand("ec", new EnderchestCommand());
        registerCommand("wb", new WorkbenchCommand());
    }
    
    /**
     * Register all event listeners.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new LocationListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }
    
    /**
     * Register a command with its executor.
     * 
     * @param command The command name
     * @param commandExecutor The command executor
     */
    private void registerCommand(String command, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(command)).setExecutor(commandExecutor);
    }
    
    /**
     * Register a command with its executor and tab completer.
     * 
     * @param command The command name
     * @param commandExecutor The command executor
     * @param tabCompleter The tab completer
     */
    private void registerCommand(String command, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
        Objects.requireNonNull(getCommand(command)).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand(command)).setTabCompleter(tabCompleter);
    }
   
    /**
     * Load configuration from file.
     */
    private void loadConfig() {
        if (this.config == null) {
            getLogger().warning("Config not initialized. Skipping load.");
            return;
        }
        
        if (!this.configFile.exists() || this.configFile.length() == 0L) {
            getLogger().warning("Config.yml not found or empty. Using default values.");
            return;
        }
        
        Locations.loadFromConfig(this.config);
        getLogger().info("Configuration loaded successfully.");
    }
    
    /**
     * Save configuration to file.
     */
    public void saveConfig() {
        if (this.config == null) {
            getLogger().warning("Config not initialized. Skipping save.");
            return;
        }
        
        Locations.saveToConfig(this.config);
        
        try {
            this.config.save(this.configFile);
            getLogger().info("Configuration saved successfully.");
        } catch (IOException e) {
            getLogger().severe("Could not save config to " + this.configFile);
            e.printStackTrace();
        }
    }
}