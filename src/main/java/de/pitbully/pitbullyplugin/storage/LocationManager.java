package de.pitbully.pitbullyplugin.storage;

import de.pitbully.pitbullyplugin.utils.ConfigManager;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Location;

/**
 * Utility class that provides static access to location storage functionality.
 * This class serves as a bridge between the old static Locations class and the new
 * LocationStorage interface implementation, enabling gradual migration.
 * 
 * <p>This manager uses the LocationStorage implementation internally but provides
 * the same static API as the original Locations class for compatibility.
 * 
 * @author Pitbully01
 * @since 1.5.1
 */
public class LocationManager {
    
    private static LocationStorage storage;
    
    /**
     * Initializes the LocationManager with a storage implementation.
     * This method must be called before any other methods are used.
     * 
     * @param locationStorage The storage implementation to use
     */
    public static void initialize(LocationStorage locationStorage) {
        storage = locationStorage;
    }
    
    /**
     * Initializes the LocationManager with appropriate storage based on configuration.
     * This method handles automatic migration if switching from file to database storage.
     * 
     * @param configManager The configuration manager
     * @param dataFolder The plugin data folder
     * @param logger Logger for messages
     */
    public static void initializeWithConfig(ConfigManager configManager, File dataFolder, Logger logger) {
        if (configManager.isDatabaseStorageEnabled()) {
            // Initialize database storage
            DatabaseConfig dbConfig = configManager.getDatabaseConfig();
            if (dbConfig == null) {
                logger.severe("Database storage is enabled but configuration is invalid! Falling back to file storage.");
                initializeFileStorage(dataFolder, logger);
                return;
            }
            
            try {
                DatabaseLocationStorage dbStorage = new DatabaseLocationStorage(dbConfig, logger);
                
                // Check if migration is needed
                File locationsFile = new File(dataFolder, "locations.yml");
                if (locationsFile.exists() && locationsFile.length() > 0) {
                    logger.info("Found existing locations.yml file. Starting migration to database...");
                    
                    // Load file storage temporarily for migration
                    FileLocationStorage fileStorage = new FileLocationStorage(dataFolder, logger);
                    fileStorage.loadAll();
                    
                    // Migrate data to database
                    dbStorage.migrateFromFileStorage(fileStorage);
                    
                    // Create backup of old file and remove it
                    createMigrationBackup(locationsFile, logger);
                    
                    fileStorage.close();
                    logger.info("Migration completed successfully! locations.yml has been backed up and cleared.");
                }
                
                storage = dbStorage;
                logger.info("Database storage initialized successfully.");
                
            } catch (Exception e) {
                logger.severe("Failed to initialize database storage: " + e.getMessage());
                logger.severe("Falling back to file storage...");
                e.printStackTrace();
                initializeFileStorage(dataFolder, logger);
            }
            
        } else {
            // Initialize file storage
            initializeFileStorage(dataFolder, logger);
        }
    }
    
    /**
     * Initializes file storage.
     */
    private static void initializeFileStorage(File dataFolder, Logger logger) {
        storage = new FileLocationStorage(dataFolder, logger);
        logger.info("File storage initialized successfully.");
    }
    
    /**
     * Creates a backup of the locations.yml file before migration.
     */
    private static void createMigrationBackup(File locationsFile, Logger logger) {
        try {
            File backupDir = new File(locationsFile.getParent(), "migration-backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new java.util.Date());
            String backupFileName = "locations_pre-migration_" + timestamp + ".yml.bak";
            File backupFile = new File(backupDir, backupFileName);
            
            java.nio.file.Files.copy(locationsFile.toPath(), backupFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Clear the original file content but keep the file for future use
            try (java.io.FileWriter writer = new java.io.FileWriter(locationsFile, false)) {
                writer.write("# This file has been migrated to database storage.\n");
                writer.write("# A backup of the original data has been created in migration-backups/\n");
                writer.write("# Database storage is now active for this plugin.\n");
            }
            
            logger.info("Migration backup created: " + backupFile.getName());
            
        } catch (Exception e) {
            logger.warning("Failed to create migration backup: " + e.getMessage());
        }
    }
    
    /**
     * Gets the current storage instance.
     * 
     * @return The current LocationStorage instance
     * @throws IllegalStateException if the manager hasn't been initialized
     */
    public static LocationStorage getStorage() {
        if (storage == null) {
            throw new IllegalStateException("LocationManager not initialized! Call initialize() first.");
        }
        return storage;
    }
    
    // Death Location Methods - matching original Locations API
    
    /**
     * Updates the last death location for a player.
     * Also updates the general last location for /back command functionality.
     * 
     * @param playerId The UUID of the player
     * @param location The death location to store
     */
    public static void updateLastDeathLocations(UUID playerId, Location location) {
        getStorage().saveDeathLocation(playerId, location);
        getStorage().saveLastLocation(playerId, location); // Also update last location
    }
    
    /**
     * Retrieves the last death location for a player.
     * 
     * @param playerId The UUID of the player
     * @return The player's last death location, or null if none exists
     */
    public static Location getLastDeathLocation(UUID playerId) {
        return getStorage().getDeathLocation(playerId);
    }
    
    // Teleport Location Methods - matching original Locations API
    
    /**
     * Updates the last teleport location for a player.
     * Also updates the general last location for /back command functionality.
     * 
     * @param playerId The UUID of the player
     * @param location The location before teleportation
     */
    public static void updateLastTeleportLocations(UUID playerId, Location location) {
        getStorage().saveTeleportLocation(playerId, location);
        getStorage().saveLastLocation(playerId, location); // Also update last location
    }
    
    /**
     * Retrieves the last teleport location for a player.
     * 
     * @param playerId The UUID of the player
     * @return The player's last teleport location, or null if none exists
     */
    public static Location getLastTeleportLocations(UUID playerId) {
        return getStorage().getTeleportLocation(playerId);
    }
    
    // Last Location Methods - matching original Locations API
    
    /**
     * Updates the general last location for a player.
     * This is used by the /back command to determine where to teleport.
     * 
     * @param playerId The UUID of the player
     * @param location The location to store as the last location
     */
    public static void updateLastLocations(UUID playerId, Location location) {
        getStorage().saveLastLocation(playerId, location);
    }
    
    /**
     * Retrieves the last known location for a player.
     * Used by the /back command for teleportation.
     * 
     * @param playerId The UUID of the player
     * @return The player's last known location, or null if none exists
     */
    public static Location getLastLocation(UUID playerId) {
        return getStorage().getLastLocation(playerId);
    }
    
    /**
     * Checks if a player has a last known location.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a last location, false otherwise
     */
    public static boolean checkLastLocation(UUID playerId) {
        return getStorage().checkLastLocation(playerId);
    }
    
    // Home Location Methods - matching original Locations API
    
    /**
     * Sets or updates a player's home location.
     * 
     * @param playerId The UUID of the player
     * @param location The location to set as the player's home
     */
    public static void updateHomeLocation(UUID playerId, Location location) {
        getStorage().saveHomeLocation(playerId, location);
    }
    
    /**
     * Retrieves a player's home location.
     * 
     * @param playerId The UUID of the player
     * @return The player's home location, or null if none is set
     */
    public static Location getHomeLocation(UUID playerId) {
        return getStorage().getHomeLocation(playerId);
    }
    
    /**
     * Checks if a player has a home location set.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a home set, false otherwise
     */
    public static boolean checkHomeLocation(UUID playerId) {
        return getStorage().hasHomeLocation(playerId);
    }
    
    /**
     * Removes a player's home location.
     * 
     * @param playerId The UUID of the player whose home to delete
     */
    public static void deleteHomeLocation(UUID playerId) {
        getStorage().deleteHomeLocation(playerId);
    }
    
    // Warp Location Methods - matching original Locations API
    
    /**
     * Creates or updates a server warp location.
     * 
     * @param warp The name of the warp
     * @param location The location for the warp
     */
    public static void updateWarpLocation(String warp, Location location) {
        getStorage().saveWarpLocation(warp, location);
    }
    
    /**
     * Retrieves a warp location by name.
     * 
     * @param warp The name of the warp
     * @return The warp location, or null if the warp doesn't exist
     */
    public static Location getWarpLocation(String warp) {
        return getStorage().getWarpLocation(warp);
    }
    
    /**
     * Gets a copy of all warp LocationManager.
     * Used for tab completion and warp listing.
     * 
     * @return A new HashMap containing all warp locations
     */
    public static Map<String, Location> getWarpHashMap() {
        return getStorage().getAllWarpLocations();
    }
    
    /**
     * Checks if a warp exists.
     * 
     * @param warp The name of the warp to check
     * @return true if the warp exists, false otherwise
     */
    public static boolean checkWarpLocation(String warp) {
        return getStorage().hasWarpLocation(warp);
    }
    
    /**
     * Removes a warp location.
     * 
     * @param warp The name of the warp to delete
     */
    public static void deleteWarpLocation(String warp) {
        getStorage().deleteWarpLocation(warp);
    }
    
    // World Spawn Location Methods - matching original Locations API
    
    /**
     * Sets the world spawn location and immediately applies it to the world.
     * 
     * @param location The location to set as world spawn
     */
    public static void updateWorldSpawnLocation(Location location) {
        getStorage().saveWorldSpawn(location);
    }
    
    /**
     * Retrieves the current world spawn location.
     * 
     * @return The world spawn location, or null if none is set
     */
    public static Location getWorldSpawnLocation() {
        return getStorage().getWorldSpawn();
    }
    
    /**
     * Checks if a world spawn location is set.
     * 
     * @return true if world spawn is set, false otherwise
     */
    public static boolean checkWorldSpawnLocation() {
        return getStorage().checkWorldSpawnLocation();
    }
    
    // Configuration Methods - matching original Locations API
    
    /**
     * Loads location data from storage.
     * This method is called during plugin startup to restore all saved data.
     */
    public static void loadFromConfig() {
        getStorage().loadAll();
    }
    
    /**
     * Saves location data to storage.
     * This method is called when data changes or during plugin shutdown.
     */
    public static void saveToConfig() {
        getStorage().saveAll();
    }
}
