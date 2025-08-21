package de.pitbully.pitbullyplugin.utils;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.storage.DatabaseConfig;
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
    
    // Database default values
    private static final String DEFAULT_STORAGE_TYPE = "file";
    private static final String DEFAULT_DATABASE_TYPE = "mysql";
    private static final String DEFAULT_DATABASE_HOST = "localhost";
    private static final int DEFAULT_DATABASE_PORT = 3306;
    private static final String DEFAULT_DATABASE_NAME = "pitbully_plugin";
    private static final String DEFAULT_DATABASE_USERNAME = "username";
    private static final String DEFAULT_DATABASE_PASSWORD = "password";
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final long DEFAULT_MAX_LIFETIME = 1800000;
    private static final boolean DEFAULT_SSL_ENABLED = false;
    private static final boolean DEFAULT_SSL_VERIFY_CERTIFICATE = true;
    
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
               !config.contains("settings.teleport.max-safe-distance") ||
               !config.contains("database.storage-type") ||
               !config.contains("database.connection.type");
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
        
        // Database defaults
        if (!config.contains("database.storage-type")) {
            config.set("database.storage-type", DEFAULT_STORAGE_TYPE);
        }
        
        if (!config.contains("database.connection.type")) {
            config.set("database.connection.type", DEFAULT_DATABASE_TYPE);
        }
        
        if (!config.contains("database.connection.host")) {
            config.set("database.connection.host", DEFAULT_DATABASE_HOST);
        }
        
        if (!config.contains("database.connection.port")) {
            config.set("database.connection.port", DEFAULT_DATABASE_PORT);
        }
        
        if (!config.contains("database.connection.database")) {
            config.set("database.connection.database", DEFAULT_DATABASE_NAME);
        }
        
        if (!config.contains("database.connection.username")) {
            config.set("database.connection.username", DEFAULT_DATABASE_USERNAME);
        }
        
        if (!config.contains("database.connection.password")) {
            config.set("database.connection.password", DEFAULT_DATABASE_PASSWORD);
        }
        
        if (!config.contains("database.connection.pool.max-connections")) {
            config.set("database.connection.pool.max-connections", DEFAULT_MAX_CONNECTIONS);
        }
        
        if (!config.contains("database.connection.pool.connection-timeout")) {
            config.set("database.connection.pool.connection-timeout", DEFAULT_CONNECTION_TIMEOUT);
        }
        
        if (!config.contains("database.connection.pool.max-lifetime")) {
            config.set("database.connection.pool.max-lifetime", DEFAULT_MAX_LIFETIME);
        }
        
        if (!config.contains("database.connection.ssl.enabled")) {
            config.set("database.connection.ssl.enabled", DEFAULT_SSL_ENABLED);
        }
        
        if (!config.contains("database.connection.ssl.verify-server-certificate")) {
            config.set("database.connection.ssl.verify-server-certificate", DEFAULT_SSL_VERIFY_CERTIFICATE);
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
    
    /**
     * Get the storage type (file or database).
     * 
     * @return the storage type
     */
    public String getStorageType() {
        return config.getString("database.storage-type", DEFAULT_STORAGE_TYPE);
    }
    
    /**
     * Check if database storage is enabled.
     * 
     * @return true if database storage is enabled, false otherwise
     */
    public boolean isDatabaseStorageEnabled() {
        return "database".equalsIgnoreCase(getStorageType());
    }
    
    /**
     * Create a DatabaseConfig object from the configuration.
     * 
     * @return DatabaseConfig object, or null if database storage is not enabled
     */
    public DatabaseConfig getDatabaseConfig() {
        if (!isDatabaseStorageEnabled()) {
            return null;
        }
        
        try {
            DatabaseConfig.DatabaseType type = DatabaseConfig.DatabaseType.fromString(
                config.getString("database.connection.type", DEFAULT_DATABASE_TYPE)
            );
            
            String host = config.getString("database.connection.host", DEFAULT_DATABASE_HOST);
            int port = config.getInt("database.connection.port", DEFAULT_DATABASE_PORT);
            String database = config.getString("database.connection.database", DEFAULT_DATABASE_NAME);
            String username = config.getString("database.connection.username", DEFAULT_DATABASE_USERNAME);
            String password = config.getString("database.connection.password", DEFAULT_DATABASE_PASSWORD);
            
            int maxConnections = config.getInt("database.connection.pool.max-connections", DEFAULT_MAX_CONNECTIONS);
            long connectionTimeout = config.getLong("database.connection.pool.connection-timeout", DEFAULT_CONNECTION_TIMEOUT);
            long maxLifetime = config.getLong("database.connection.pool.max-lifetime", DEFAULT_MAX_LIFETIME);
            
            boolean sslEnabled = config.getBoolean("database.connection.ssl.enabled", DEFAULT_SSL_ENABLED);
            boolean sslVerifyServerCertificate = config.getBoolean("database.connection.ssl.verify-server-certificate", DEFAULT_SSL_VERIFY_CERTIFICATE);
            
            return new DatabaseConfig(type, host, port, database, username, password,
                maxConnections, connectionTimeout, maxLifetime, sslEnabled, sslVerifyServerCertificate);
                
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Invalid database type in configuration: " + e.getMessage());
            return null;
        }
    }
}
