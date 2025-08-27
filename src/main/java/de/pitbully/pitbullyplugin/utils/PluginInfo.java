package de.pitbully.pitbullyplugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility class for accessing plugin information that is populated at build time.
 * This class reads version and build information from plugin-info.properties
 * which is automatically generated during the Maven build process.
 * 
 * @author Pitbully01
 * @since 1.5.3
 */
public class PluginInfo {
    
    private static final Properties properties = new Properties();
    private static boolean loaded = false;
    private static Logger logger;
    
    // Default fallback values
    private static final String DEFAULT_VERSION = "1.6.1-SNAPSHOT";
    private static final String DEFAULT_NAME = "PitbullyPlugin";
    private static final String DEFAULT_DESCRIPTION = "A Minecraft plugin providing teleportation commands";
    
    /**
     * Initialize the PluginInfo with a logger instance.
     * This should be called once during plugin startup.
     * 
     * @param pluginLogger The logger to use for error reporting
     */
    public static void initialize(Logger pluginLogger) {
        logger = pluginLogger;
        loadProperties();
    }
    
    /**
     * Load properties from the plugin-info.properties file.
     */
    private static void loadProperties() {
        if (loaded) {
            return;
        }
        
        try (InputStream inputStream = PluginInfo.class.getClassLoader()
                .getResourceAsStream("plugin-info.properties")) {
            
            if (inputStream == null) {
                logWarning("plugin-info.properties not found, using default values");
                return;
            }
            
            properties.load(inputStream);
            loaded = true;
            logDebug("Plugin information loaded successfully");
            
        } catch (IOException e) {
            logWarning("Failed to load plugin-info.properties: " + e.getMessage());
        }
    }
    
    /**
     * Get the plugin version.
     * 
     * @return The plugin version from pom.xml, or a default value if not available
     */
    public static String getVersion() {
        return getProperty("plugin.version", DEFAULT_VERSION);
    }
    
    /**
     * Get the plugin name.
     * 
     * @return The plugin name from pom.xml, or a default value if not available
     */
    public static String getName() {
        return getProperty("plugin.name", DEFAULT_NAME);
    }
    
    /**
     * Get the plugin description.
     * 
     * @return The plugin description from pom.xml, or a default value if not available
     */
    public static String getDescription() {
        return getProperty("plugin.description", DEFAULT_DESCRIPTION);
    }
    
    /**
     * Get the plugin URL.
     * 
     * @return The plugin URL from pom.xml, or null if not available
     */
    public static String getUrl() {
        return getProperty("plugin.url", null);
    }
    
    /**
     * Get the plugin group ID.
     * 
     * @return The plugin group ID from pom.xml, or null if not available
     */
    public static String getGroupId() {
        return getProperty("plugin.groupId", null);
    }
    
    /**
     * Get the plugin artifact ID.
     * 
     * @return The plugin artifact ID from pom.xml, or null if not available
     */
    public static String getArtifactId() {
        return getProperty("plugin.artifactId", null);
    }
    
    /**
     * Get the build timestamp.
     * 
     * @return The build timestamp, or null if not available
     */
    public static String getBuildTimestamp() {
        return getProperty("build.timestamp", null);
    }
    
    /**
     * Get the Java version used for building.
     * 
     * @return The Java version used for building, or null if not available
     */
    public static String getBuildJavaVersion() {
        return getProperty("build.java.version", null);
    }
    
    /**
     * Get a full version string including build information for debugging.
     * 
     * @return A comprehensive version string
     */
    public static String getFullVersionInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" v").append(getVersion());
        
        String timestamp = getBuildTimestamp();
        if (timestamp != null) {
            sb.append(" (built: ").append(timestamp).append(")");
        }
        
        String javaVersion = getBuildJavaVersion();
        if (javaVersion != null) {
            sb.append(" [Java ").append(javaVersion).append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * Get a property value with fallback.
     * 
     * @param key The property key
     * @param defaultValue The default value if property is not found
     * @return The property value or default value
     */
    private static String getProperty(String key, String defaultValue) {
        if (!loaded) {
            loadProperties();
        }
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Log a warning message if logger is available.
     */
    private static void logWarning(String message) {
        if (logger != null) {
            logger.warning("[PluginInfo] " + message);
        }
    }
    
    /**
     * Log a debug message if logger is available.
     */
    private static void logDebug(String message) {
        if (logger != null) {
            logger.info("[PluginInfo] " + message);
        }
    }
}
