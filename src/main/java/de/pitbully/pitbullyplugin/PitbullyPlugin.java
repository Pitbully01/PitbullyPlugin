package de.pitbully.pitbullyplugin;

import de.pitbully.pitbullyplugin.commands.BackCommand;
import de.pitbully.pitbullyplugin.commands.DelHomeCommand;
import de.pitbully.pitbullyplugin.commands.DelWarpCommand;
import de.pitbully.pitbullyplugin.commands.EnderchestCommand;
import de.pitbully.pitbullyplugin.commands.HomeCommand;
import de.pitbully.pitbullyplugin.commands.PluginInfoCommand;
import de.pitbully.pitbullyplugin.commands.SetHomeCommand;
import de.pitbully.pitbullyplugin.commands.SetWarpCommand;
import de.pitbully.pitbullyplugin.commands.SetWorldSpawnCommand;
import de.pitbully.pitbullyplugin.commands.TabCompleters.BackTabCompleter;
import de.pitbully.pitbullyplugin.commands.TabCompleters.WarpTabCompleter;
import de.pitbully.pitbullyplugin.commands.WarpCommand;
import de.pitbully.pitbullyplugin.commands.WorkbenchCommand;
import de.pitbully.pitbullyplugin.listeners.LocationListener;
import de.pitbully.pitbullyplugin.listeners.PlayerDeathListener;
import de.pitbully.pitbullyplugin.storage.FileLocationStorage;
import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.storage.LocationStorage;
import de.pitbully.pitbullyplugin.utils.ConfigManager;
import de.pitbully.pitbullyplugin.utils.PluginInfo;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

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
 * @version 1.5.3
 * @since 1.0.0
 */
public final class PitbullyPlugin extends JavaPlugin {
    
    /** The singleton instance of the plugin */
    private static PitbullyPlugin instance;
    
    /** The configuration file handle */
    private File configFile;
    
    /** The loaded configuration */
    private FileConfiguration config;
    
    /** The location storage implementation */
    private LocationStorage locationStorage;
    
    /** The configuration manager */
    private ConfigManager configManager;
    
    /** Auto-save task ID for periodic saving */
    private int autoSaveTaskId = -1;
    
    /**
     * Called when the plugin is enabled.
     * Initializes all plugin components and loads configuration.
     */
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize plugin information system
        PluginInfo.initialize(getLogger());
        
        getLogger().info("Initializing " + PluginInfo.getFullVersionInfo() + "...");
        
        // Step 1: Initialize basic config file structure
        initConfig();
        
        // Step 2: Initialize config manager first (needed for storage type detection)
        initConfigManager();
        
        // Step 3: Initialize location storage with automatic migration support
        initLocationStorage();
        
        // Step 4: Register commands and events
        registerCommands();
        registerEvents();
        
        // Step 5: Load final configuration
        loadConfig();
        
        // Step 6: Start auto-save task (save every 5 minutes)
        startAutoSaveTask();
        
        // Step 7: Perform initial save to ensure all configs are written
        saveConfig();
        
        getLogger().info(PluginInfo.getName() + " successfully enabled! " + 
            (configManager.isDatabaseStorageEnabled() ? "Database" : "File") + " storage active.");
    }
    
    /**
     * Called when the plugin is disabled.
     * Saves configuration and closes storage connections before shutdown.
     */
    @Override
    public void onDisable() {
        // Cancel auto-save task
        if (autoSaveTaskId != -1) {
            getServer().getScheduler().cancelTask(autoSaveTaskId);
            autoSaveTaskId = -1;
        }
        
        // Save and close storage properly
        if (locationStorage != null) {
            locationStorage.close();
        }
        
        saveConfig();
        getLogger().info(PluginInfo.getName() + " has been disabled!");
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
     * Get the configuration manager instance.
     * 
     * @return The configuration manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Initialize the configuration file.
     * Creates the config file handle and loads it if it exists.
     */
    private void initConfig() {
        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        this.configFile = new File(getDataFolder(), "config.yml");
        
        // Create config from default resource if it doesn't exist
        if (!this.configFile.exists()) {
            saveDefaultConfig();
            if (configManager != null && configManager.isDebugModeEnabled()) {
                getLogger().info("[DEBUG] Created new config.yml from defaults.");
            }
        }
        
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        
        // Ensure config is properly loaded and not empty
        if (this.config == null) {
            this.config = new YamlConfiguration();
            getLogger().warning("Config was null, created new YamlConfiguration.");
        }
    }
    
    /**
     * Initialize the configuration manager.
     * Creates the config manager instance that handles all plugin settings.
     */
    private void initConfigManager() {
        this.configManager = new ConfigManager(this);
    }
    
    /**
     * Initialize the location storage system.
     * Creates the appropriate storage implementation based on configuration.
     * Handles automatic migration from file to database if needed.
     */
    private void initLocationStorage() {
        try {
            // Use the new configuration-based initialization with automatic migration
            LocationManager.initializeWithConfig(configManager, getDataFolder(), getLogger());
            
            // Get the initialized storage from LocationManager
            this.locationStorage = LocationManager.getStorage();
            
            if (configManager.isDatabaseStorageEnabled()) {
                if (configManager.isDebugModeEnabled()) {
                    getLogger().info("[DEBUG] Database storage initialized successfully.");
                }
            } else {
                if (configManager.isDebugModeEnabled()) {
                    getLogger().info("[DEBUG] File storage initialized successfully.");
                }
            }
            
        } catch (Exception e) {
            getLogger().severe("Failed to initialize location storage: " + e.getMessage());
            getLogger().log(Level.SEVERE, "Exception while initializing location storage", e);
            
            // Fallback to file storage
            getLogger().info("Falling back to file storage...");
            this.locationStorage = new FileLocationStorage(getDataFolder(), getLogger());
            LocationManager.initialize(this.locationStorage);
        }
    }

    
    /**
     * Register all plugin commands.
     * Sets up command executors and tab completers for all plugin commands.
     * Includes both primary commands and their aliases.
     */
    private void registerCommands() {
        BackTabCompleter backTabCompleter = new BackTabCompleter();
        WarpTabCompleter warpTabCompleter = new WarpTabCompleter();
        
        registerCommand("home", new HomeCommand());
        registerCommand("sethome", new SetHomeCommand());
        registerCommand("delhome", new DelHomeCommand());
        registerCommand("back", new BackCommand(), backTabCompleter);
        registerCommand("setwarp", new SetWarpCommand());
        registerCommand("warp", new WarpCommand(), warpTabCompleter);
        registerCommand("delwarp", new DelWarpCommand(), warpTabCompleter);
        registerCommand("enderchest", new EnderchestCommand());
        registerCommand("workbench", new WorkbenchCommand());
        registerCommand("setspawn", new SetWorldSpawnCommand());
        registerCommand("pitbullyinfo", new PluginInfoCommand());
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
        if (this.config == null || this.configFile == null) {
            getLogger().warning("Config not initialized. Initializing now...");
            initConfig();
        }
        
        if (!this.configFile.exists() || this.configFile.length() == 0L) {
            if (configManager != null && configManager.isDebugModeEnabled()) {
                getLogger().info("[DEBUG] Config.yml not found or empty. Creating new configuration with default values.");
            }
            // Create parent directories if they don't exist
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            
            // Let ConfigManager handle the defaults
            if (configManager != null) {
                configManager.loadConfig();
            }
            
            if (configManager != null && configManager.isDebugModeEnabled()) {
                getLogger().info("[DEBUG] Default configuration created. Will be saved with location data.");
            }
            return;
        }
        
        try {
            // Reload from file
            this.config = YamlConfiguration.loadConfiguration(this.configFile);
            
            // Let ConfigManager reload its settings
            if (configManager != null) {
                configManager.reload();
            }
            
            // Load location data
            if (LocationManager.getStorage() != null) {
                LocationManager.loadFromConfig();
            }
            
            if (configManager != null && configManager.isDebugModeEnabled()) {
                getLogger().info("[DEBUG] Configuration loaded successfully from " + configFile.getName());
            }
        } catch (Exception e) {
            getLogger().severe("Error loading configuration: " + e.getMessage());
            getLogger().log(Level.SEVERE, "Exception while loading configuration", e);
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
        if (this.config == null || this.configFile == null) {
            getLogger().warning("Config not initialized. Initializing now...");
            initConfig();
            if (this.config == null) {
                getLogger().severe("Failed to initialize config. Cannot save.");
                return;
            }
        }
        
        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Save location data first (but only if LocationManager is initialized)
        try {
            if (LocationManager.getStorage() != null) {
                LocationManager.saveToConfig();
            } else {
                if (configManager != null && configManager.isDebugModeEnabled()) {
                    getLogger().info("[DEBUG] LocationManager not yet initialized, skipping location data save.");
                }
            }
        } catch (IllegalStateException e) {
            // LocationManager not initialized yet, that's fine during startup
            if (configManager != null && configManager.isDebugModeEnabled()) {
                getLogger().info("[DEBUG] Saving config without location data (LocationManager not yet ready).");
            }
        }
        
        try {
            this.config.save(this.configFile);
            if (configManager != null && configManager.isDebugModeEnabled()) {
                getLogger().info("[DEBUG] Configuration saved successfully to " + this.configFile.getName());
            }
        } catch (IOException e) {
            getLogger().severe("Could not save config to " + this.configFile + ": " + e.getMessage());
            getLogger().log(Level.SEVERE, "Exception while saving configuration", e);
        }
    }
    
    /**
     * Starts the auto-save task that periodically saves configuration and location data.
     * Saves data every 5 minutes (6000 ticks) to prevent data loss.
     */
    private void startAutoSaveTask() {
        // Cancel existing task if running
        if (autoSaveTaskId != -1) {
            getServer().getScheduler().cancelTask(autoSaveTaskId);
        }
        
        // Start new auto-save task (every 5 minutes = 6000 ticks)
        autoSaveTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    saveConfig();
                    if (configManager != null && configManager.isDebugModeEnabled()) {
                        getLogger().info("[DEBUG] Auto-save completed successfully.");
                    }
                } catch (Exception e) {
                    getLogger().warning("Error during auto-save: " + e.getMessage());
                }
            }
        }, 6000L, 6000L); // Initial delay: 5 minutes, Repeat every: 5 minutes
        
        if (configManager != null && configManager.isDebugModeEnabled()) {
            getLogger().info("[DEBUG] Auto-save task started (saves every 5 minutes).");
        }
    }
}
