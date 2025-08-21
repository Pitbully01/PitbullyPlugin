package de.pitbully.pitbullyplugin.utils;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

/**
 * Configuration manager for PitbullyPlugin.
 * Handles loading, saving, and accessing plugin configuration values.
 * 
 * <p>This class provides a centralized way to manage all plugin settings
 * from the config.yml file, including:
 * <ul>
 * <li>Backup settings</li>
 * <li>Debug mode</li>
 * <li>Teleportation safety settings</li>
 * </ul>
 * 
 * @author Pitbully01
 * @version 1.5.1
 * @since 1.5.1
 */
public class ConfigManager {
    
    private final PitbullyPlugin plugin;
    private FileConfiguration config;
    
    // Default values
    private static final boolean DEFAULT_CREATE_BACKUPS = true;
    private static final boolean DEFAULT_DEBUG_MODE = false;
    private static final boolean DEFAULT_SAFETY_CHECK = true;
    private static final int DEFAULT_MAX_SAFE_DISTANCE = 10;
    
    /**
     * Creates a new ConfigManager instance.
     * 
     * @param plugin The plugin instance
     */
    public ConfigManager(PitbullyPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    /**
     * Load configuration from file and set defaults if needed.
     * This method is safe to call after location migration.
     */
    public void loadConfig() {
        // Only save default config if the file doesn't exist yet
        if (!plugin.getDataFolder().exists() || !new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        
        // Set default values if they don't exist (but don't overwrite migration cleanup)
        setDefaults();
        
        // Only save if we actually added new settings
        if (hasNewSettings()) {
            plugin.saveConfig();
        }
    }
    
    /**
     * Check if new settings were added that need to be saved.
     * 
     * @return true if new settings were added
     */
    private boolean hasNewSettings() {
        return !config.contains("settings.create-backups") ||
               !config.contains("settings.debug-mode") ||
               !config.contains("settings.teleport.safety-check") ||
               !config.contains("settings.teleport.max-safe-distance");
    }
    
    /**
     * Set default configuration values if they don't exist.
     */
    private void setDefaults() {
        if (!config.contains("settings.create-backups")) {
            config.set("settings.create-backups", DEFAULT_CREATE_BACKUPS);
        }
        
        if (!config.contains("settings.debug-mode")) {
            config.set("settings.debug-mode", DEFAULT_DEBUG_MODE);
        }
        
        if (!config.contains("settings.teleport.safety-check")) {
            config.set("settings.teleport.safety-check", DEFAULT_SAFETY_CHECK);
        }
        
        if (!config.contains("settings.teleport.max-safe-distance")) {
            config.set("settings.teleport.max-safe-distance", DEFAULT_MAX_SAFE_DISTANCE);
        }
    }
    
    /**
     * Check if backup creation is enabled.
     * 
     * @return true if backups should be created, false otherwise
     */
    public boolean isCreateBackupsEnabled() {
        return config.getBoolean("settings.create-backups", DEFAULT_CREATE_BACKUPS);
    }
    
    /**
     * Check if debug mode is enabled.
     * 
     * @return true if debug mode is enabled, false otherwise
     */
    public boolean isDebugModeEnabled() {
        return config.getBoolean("settings.debug-mode", DEFAULT_DEBUG_MODE);
    }
    
    /**
     * Check if teleportation safety checks are enabled.
     * 
     * @return true if safety checks should be performed, false otherwise
     */
    public boolean isSafetyCheckEnabled() {
        return config.getBoolean("settings.teleport.safety-check", DEFAULT_SAFETY_CHECK);
    }
    
    /**
     * Get the maximum distance to search for safe teleport locations.
     * 
     * @return the maximum safe distance in blocks
     */
    public int getMaxSafeDistance() {
        return config.getInt("settings.teleport.max-safe-distance", DEFAULT_MAX_SAFE_DISTANCE);
    }
    
    /**
     * Log a debug message if debug mode is enabled.
     * 
     * @param message the debug message to log
     */
    public void debug(String message) {
        if (isDebugModeEnabled()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
    
    /**
     * Reload the configuration from file.
     */
    public void reload() {
        loadConfig();
    }
}
