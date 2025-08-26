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
 * <li>Debug mode</li>
 * <li>Teleportation safety settings</li>
 * </ul>
 */
public class ConfigManager {

    private final PitbullyPlugin plugin;
    private FileConfiguration config;
    // Save to disk only when we added missing keys
    private boolean defaultsAdded = false;

    // Default values
    private static final boolean DEFAULT_DEBUG_MODE = false;
    private static final boolean DEFAULT_SAFETY_CHECK = true;
    private static final int DEFAULT_MAX_SAFE_DISTANCE = 10;
    // TPA defaults
    private static final int DEFAULT_TPA_REQUEST_TIMEOUT = 30;

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

    public ConfigManager(PitbullyPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Load configuration from file and set defaults if needed.
     * Only writes to disk if missing keys were added.
     */
    public void loadConfig() {
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Create default config if missing
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        plugin.reloadConfig();
        this.config = plugin.getConfig();

        // Set default values if they don't exist (don't overwrite existing)
        setDefaults();

        // Persist only when we actually added missing keys
        saveIfDefaultsAdded();

        if (isDebugModeEnabled()) {
            plugin.getLogger().info("[DEBUG] Configuration loaded successfully." + (defaultsAdded ? " (missing keys added)" : ""));
        }
    }

    /**
     * Set default configuration values if they don't exist.
     */
    private void setDefaults() {
        defaultsAdded = false;

        if (!config.contains("settings.debug-mode")) {
            config.set("settings.debug-mode", DEFAULT_DEBUG_MODE);
            defaultsAdded = true;
        }

        if (!config.contains("settings.teleport.safety-check")) {
            config.set("settings.teleport.safety-check", DEFAULT_SAFETY_CHECK);
            defaultsAdded = true;
        }

        if (!config.contains("settings.teleport.max-safe-distance")) {
            config.set("settings.teleport.max-safe-distance", DEFAULT_MAX_SAFE_DISTANCE);
            defaultsAdded = true;
        }

        // TPA default values
        if (!config.contains("settings.tpa.request-timeout-seconds")) {
            config.set("settings.tpa.request-timeout-seconds", DEFAULT_TPA_REQUEST_TIMEOUT);
            defaultsAdded = true;
        }

        // Database defaults
        if (!config.contains("database.storage-type")) {
            config.set("database.storage-type", DEFAULT_STORAGE_TYPE);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.type")) {
            config.set("database.connection.type", DEFAULT_DATABASE_TYPE);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.host")) {
            config.set("database.connection.host", DEFAULT_DATABASE_HOST);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.port")) {
            config.set("database.connection.port", DEFAULT_DATABASE_PORT);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.database")) {
            config.set("database.connection.database", DEFAULT_DATABASE_NAME);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.username")) {
            config.set("database.connection.username", DEFAULT_DATABASE_USERNAME);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.password")) {
            config.set("database.connection.password", DEFAULT_DATABASE_PASSWORD);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.pool.max-connections")) {
            config.set("database.connection.pool.max-connections", DEFAULT_MAX_CONNECTIONS);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.pool.connection-timeout")) {
            config.set("database.connection.pool.connection-timeout", DEFAULT_CONNECTION_TIMEOUT);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.pool.max-lifetime")) {
            config.set("database.connection.pool.max-lifetime", DEFAULT_MAX_LIFETIME);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.ssl.enabled")) {
            config.set("database.connection.ssl.enabled", DEFAULT_SSL_ENABLED);
            defaultsAdded = true;
        }

        if (!config.contains("database.connection.ssl.verify-server-certificate")) {
            config.set("database.connection.ssl.verify-server-certificate", DEFAULT_SSL_VERIFY_CERTIFICATE);
            defaultsAdded = true;
        }
    }

    /**
     * Reload the configuration from file and add any missing defaults (saving only if needed).
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        setDefaults();
        saveIfDefaultsAdded();
    }

    /** Saves the config only when we actually added missing defaults. */
    private void saveIfDefaultsAdded() {
        if (defaultsAdded) {
            try {
                plugin.saveConfig();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to save config after adding missing defaults: " + e.getMessage());
            } finally {
                // Reset flag after save attempt to avoid repeated writes
                defaultsAdded = false;
            }
        }
    }

    public boolean isDebugModeEnabled() {
        return config.getBoolean("settings.debug-mode", DEFAULT_DEBUG_MODE);
    }

    public boolean isSafetyCheckEnabled() {
        return config.getBoolean("settings.teleport.safety-check", DEFAULT_SAFETY_CHECK);
    }

    public int getMaxSafeDistance() {
        return config.getInt("settings.teleport.max-safe-distance", DEFAULT_MAX_SAFE_DISTANCE);
    }

    /**
     * Get the TPA request timeout in seconds.
     * @return timeout in seconds (default 30)
     */
    public int getTpaRequestTimeout() {
        return config.getInt("settings.tpa.request-timeout-seconds", DEFAULT_TPA_REQUEST_TIMEOUT);
    }

    public void debug(String message) {
        if (isDebugModeEnabled()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public String getStorageType() {
        return config.getString("database.storage-type", DEFAULT_STORAGE_TYPE);
    }

    public boolean isDatabaseStorageEnabled() {
        return "database".equalsIgnoreCase(getStorageType());
    }

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
