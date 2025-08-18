package de.pitbully.pitbullyplugin;

import de.pitbully.pitbullyplugin.commands.BackCommand;
import de.pitbully.pitbullyplugin.commands.DelHomeCommand;
import de.pitbully.pitbullyplugin.commands.DelWarpCommand;
import de.pitbully.pitbullyplugin.commands.EnderchestCommand;
import de.pitbully.pitbullyplugin.commands.HomeCommand;
import de.pitbully.pitbullyplugin.commands.SetHomeCommand;
import de.pitbully.pitbullyplugin.commands.SetWarpCommand;
import de.pitbully.pitbullyplugin.commands.SetWorldSpawnCommand;
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
 * 
 * <p>This class serves as the entry point for the PitbullyPlugin and manages:
 * <ul>
 * <li>Plugin lifecycle (enable/disable)</li>
 * <li>Command registration and tab completion</li>
 * <li>Event listener registration</li>
 * <li>Configuration file management</li>
 * <li>Plugin instance management (Singleton pattern)</li>
 * </ul>
 * 
 * <p>The plugin provides teleportation functionality including homes, warps,
 * back command, and utility commands like enderchest and workbench access.
 * 
 * @author Pitbully01
 * @version 1.4.4
 * @since 1.0.0
 */
public final class PitbullyPlugin extends JavaPlugin {
    
    /** The singleton instance of the plugin */
    private static PitbullyPlugin instance;
    
    /** The configuration file handle */
    private File configFile;
    
    /** The loaded configuration */
    private FileConfiguration config;
    
    /**
     * Called when the plugin is enabled.
     * Initializes all plugin components and loads configuration.
     */
    @Override
    public void onEnable() {
        instance = this;
        
        initConfig();
        registerCommands();
        registerEvents();
        loadConfig();
        
        getLogger().info("PitbullyPlugin has been enabled!");
    }
    
    /**
     * Called when the plugin is disabled.
     * Saves configuration before shutdown.
     */
    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("PitbullyPlugin has been disabled!");
    }
    
    /**
     * Get the plugin instance.
     * Used for accessing the plugin from other classes in a singleton pattern.
     * 
     * @return The plugin instance, or null if not yet initialized
     */
    public static PitbullyPlugin getInstance() {
        return instance;
    }
    
    /**
     * Initialize the configuration file.
     * Creates the config file handle and loads it if it exists.
     */
    private void initConfig() {
        if (this.config == null) {
            this.configFile = new File(getDataFolder(), "config.yml");
            this.config = YamlConfiguration.loadConfiguration(this.configFile);
        }
    }
    
    /**
     * Register all plugin commands.
     * Sets up command executors and tab completers for all plugin commands.
     * Includes both primary commands and their aliases.
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
        registerCommand("setspawn", new SetWorldSpawnCommand());
        
        // Register aliases manually to ensure they work
        registerCommand("ec", new EnderchestCommand());
        registerCommand("wb", new WorkbenchCommand());
    }
    
    /**
     * Register all event listeners.
     * Sets up listeners for location tracking and player death events.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new LocationListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }
    
    /**
     * Register a command with its executor.
     * Helper method to simplify command registration.
     * 
     * @param command The command name as defined in plugin.yml
     * @param commandExecutor The command executor instance
     * @throws NullPointerException if the command is not defined in plugin.yml
     */
    private void registerCommand(String command, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(command)).setExecutor(commandExecutor);
    }
    
    /**
     * Register a command with its executor and tab completer.
     * Helper method for commands that require tab completion functionality.
     * 
     * @param command The command name as defined in plugin.yml
     * @param commandExecutor The command executor instance
     * @param tabCompleter The tab completer instance
     * @throws NullPointerException if the command is not defined in plugin.yml
     */
    private void registerCommand(String command, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
        Objects.requireNonNull(getCommand(command)).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand(command)).setTabCompleter(tabCompleter);
    }
   
    /**
     * Load configuration from file.
     * Handles initial config loading with error handling and default value creation.
     * Creates a new config file if none exists.
     */
    private void loadConfig() {
        if (this.config == null) {
            getLogger().warning("Config not initialized. Skipping load.");
            return;
        }
        
        if (!this.configFile.exists() || this.configFile.length() == 0L) {
            getLogger().info("Config.yml not found or empty. Creating new configuration with default values.");
            // Create parent directories if they don't exist
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            saveConfig(); // Save empty config to create the file
            return;
        }
        
        try {
            Locations.loadFromConfig(this.config);
            getLogger().info("Configuration loaded successfully.");
        } catch (Exception e) {
            getLogger().severe("Error loading configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Save configuration to file.
     * Persists all location data to the config file with error handling.
     * Creates necessary directories if they don't exist.
     * 
     * <p>This method is called:
     * <ul>
     * <li>When the plugin is disabled</li>
     * <li>After location data changes (homes, warps, etc.)</li>
     * <li>When creating a new config file</li>
     * </ul>
     */
    public void saveConfig() {
        if (this.config == null) {
            getLogger().warning("Config not initialized. Skipping save.");
            return;
        }
        
        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        Locations.saveToConfig(this.config);
        
        try {
            this.config.save(this.configFile);
            getLogger().info("Configuration saved successfully.");
        } catch (IOException e) {
            getLogger().severe("Could not save config to " + this.configFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}