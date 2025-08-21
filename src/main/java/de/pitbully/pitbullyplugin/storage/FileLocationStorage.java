package de.pitbully.pitbullyplugin.storage;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * File-based implementation of LocationStorage.
 * 
 * <p>This implementation stores all location data in a separate locations.yml file
 * using Bukkit's built-in YAML configuration system. Data is loaded into memory
 * at startup and saved back to the file when needed.
 * 
 * <p>This separation allows the main locationsConfig.yml to be used for actual plugin
 * configuration while keeping location data in its own dedicated file.
 * 
 * @author Pitbully01
 * @since 1.5.1
 */
public class FileLocationStorage implements LocationStorage {
    
    private final FileConfiguration locationsConfig;
    private final File locationsFile;
    private final Logger logger;
    
    // In-memory storage maps (wie in der ursprünglichen Locations Klasse)
    private final Map<UUID, Location> deathLocations = new HashMap<>();
    private final Map<UUID, Location> teleportLocations = new HashMap<>();
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private final Map<UUID, Location> homeLocations = new HashMap<>();
    private final Map<String, Location> warpLocations = new HashMap<>();
    private Location worldSpawn;
    
    /**
     * Creates a new FileLocationStorage instance.
     * 
     * @param dataFolder The plugin's data folder where locations.yml will be stored
     * @param logger Logger for error reporting
     */
    public FileLocationStorage(File dataFolder, Logger logger) {
        this.logger = logger;
        this.locationsFile = new File(dataFolder, "locations.yml");
        
        // Ensure the data folder exists
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        // Load or create the locations configuration
        this.locationsConfig = YamlConfiguration.loadConfiguration(this.locationsFile);
        
        logger.info("FileLocationStorage initialized with locations file: " + this.locationsFile.getAbsolutePath());
    }
    
    /**
     * Creates a new FileLocationStorage instance with migration from existing config.
     * This constructor is used to migrate data from the old config.yml format.
     * 
     * @param dataFolder The plugin's data folder where locations.yml will be stored
     * @param logger Logger for error reporting
     * @param oldConfig The old configuration to migrate from (optional)
     */
    public FileLocationStorage(File dataFolder, Logger logger, FileConfiguration oldConfig) {
        this(dataFolder, logger);
        
        // If there's an old config and no existing locations file, migrate the data
        if (oldConfig != null && !locationsFile.exists() && hasLocationData(oldConfig)) {
            logger.info("Migrating location data from config.yml to locations.yml...");
            migrateFromOldConfig(oldConfig);
            saveConfigFile();
            logger.info("Migration completed successfully!");
        }
    }
    
    /**
     * Checks if the old configuration contains location data.
     * 
     * @param config The configuration to check
     * @return true if location data is found
     */
    private boolean hasLocationData(FileConfiguration config) {
        return config.contains("lastDeathLocations") || 
               config.contains("lastTeleportLocations") || 
               config.contains("lastLocations") || 
               config.contains("homeLocations") || 
               config.contains("warpLocations") || 
               config.contains("worldSpawnLocation");
    }
    
    /**
     * Migrates location data from the old config format to the new locations file.
     * 
     * @param oldConfig The old configuration containing location data
     */
    private void migrateFromOldConfig(FileConfiguration oldConfig) {
        // Copy all location sections to the new config
        for (String section : new String[]{"lastDeathLocations", "lastTeleportLocations", 
                                          "lastLocations", "homeLocations", "warpLocations"}) {
            if (oldConfig.contains(section)) {
                locationsConfig.set(section, oldConfig.get(section));
            }
        }
        
        // Copy world spawn location
        if (oldConfig.contains("worldSpawnLocation")) {
            locationsConfig.set("worldSpawnLocation", oldConfig.get("worldSpawnLocation"));
        }
    }

    /**
     * Saves a player's death location.
     * 
     * @param playerId The UUID of the player who died
     * @param location The location where the player died
     */
    @Override
    public void saveDeathLocation(UUID playerId, Location location) {
        deathLocations.put(playerId, location);
    }

    /**
     * Retrieves a player's last death location.
     * 
     * @param playerId The UUID of the player
     * @return The player's last death location, or null if none exists
     */
    @Override
    public Location getDeathLocation(UUID playerId) {
        return deathLocations.get(playerId);
    }

    /**
     * Retrieves all stored death LocationManager.
     * 
     * @return A map of player UUIDs to their death locations
     */
    @Override
    public Map<UUID, Location> getAllDeathLocations() {
        return new HashMap<>(deathLocations);
    }

    /**
     * Saves a player's location before teleportation.
     * 
     * @param playerId The UUID of the player
     * @param location The location before the teleportation occurred
     */
    @Override
    public void saveTeleportLocation(UUID playerId, Location location) {
        teleportLocations.put(playerId, location);
    }

    /**
     * Retrieves a player's last teleport location.
     * 
     * @param playerId The UUID of the player
     * @return The player's last teleport location, or null if none exists
     */
    @Override
    public Location getTeleportLocation(UUID playerId) {
        return teleportLocations.get(playerId);
    }

    /**
     * Retrieves all stored teleport LocationManager.
     * 
     * @return A map of player UUIDs to their teleport locations
     */
    @Override
    public Map<UUID, Location> getAllTeleportLocations() {
        return new HashMap<>(teleportLocations);
    }

    /**
     * Saves a player's general last location.
     * This is typically used as a fallback for the /back command.
     * 
     * @param playerId The UUID of the player
     * @param location The location to save as the last location
     */
    @Override
    public void saveLastLocation(UUID playerId, Location location) {
        lastLocations.put(playerId, location);
    }

    /**
     * Retrieves a player's last known location.
     * 
     * @param playerId The UUID of the player
     * @return The player's last known location, or null if none exists
     */
    @Override
    public Location getLastLocation(UUID playerId) {
        return lastLocations.get(playerId);
    }

    /**
     * Retrieves all stored last LocationManager.
     * 
     * @return A map of player UUIDs to their last locations
     */
    @Override
    public Map<UUID, Location> getAllLastLocations() {
        return new HashMap<>(lastLocations);
    }

    /**
     * Saves a player's home location.
     * 
     * @param playerId The UUID of the player
     * @param location The location to set as the player's home
     */
    @Override
    public void saveHomeLocation(UUID playerId, Location location) {
        homeLocations.put(playerId, location);
    }

    /**
     * Retrieves a player's home location.
     * 
     * @param playerId The UUID of the player
     * @return The player's home location, or null if none is set
     */
    @Override
    public Location getHomeLocation(UUID playerId) {
        return homeLocations.get(playerId);
    }

    /**
     * Checks if a player has a home location set.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a home location, false otherwise
     */
    @Override
    public boolean hasHomeLocation(UUID playerId) {
        return homeLocations.containsKey(playerId);
    }

    /**
     * Removes a player's home location.
     * 
     * @param playerId The UUID of the player whose home to delete
     */
    @Override
    public void deleteHomeLocation(UUID playerId) {
        homeLocations.remove(playerId);
    }

    /**
     * Retrieves all stored home LocationManager.
     * 
     * @return A map of player UUIDs to their home locations
     */
    @Override
    public Map<UUID, Location> getAllHomeLocations() {
        return new HashMap<>(homeLocations);
    }


    /**
     * Saves a warp location with the given name.
     * 
     * @param warpName The name of the warp
     * @param location The location for the warp
     */
    @Override
    public void saveWarpLocation(String warpName, Location location) {
        warpLocations.put(warpName, location);
    }

    /**
     * Retrieves a warp location by name.
     * 
     * @param warpName The name of the warp
     * @return The warp location, or null if the warp doesn't exist
     */
    @Override
    public Location getWarpLocation(String warpName) {
        return warpLocations.get(warpName);
    }

    /**
     * Checks if a warp with the given name exists.
     * 
     * @param warpName The name of the warp to check
     * @return true if the warp exists, false otherwise
     */
    @Override
    public boolean hasWarpLocation(String warpName) {
        return warpLocations.containsKey(warpName);
    }

    /**
     * Removes a warp location.
     * 
     * @param warpName The name of the warp to delete
     */
    @Override
    public void deleteWarpLocation(String warpName) {
        warpLocations.remove(warpName);
    }

    /**
     * Retrieves all stored warp LocationManager.
     * 
     * @return A map of warp names to their locations
     */
    @Override
    public Map<String, Location> getAllWarpLocations() {
        return new HashMap<>(warpLocations);
    }


    /**
     * Saves the world spawn location and immediately applies it to the world.
     * 
     * @param location The location to set as world spawn
     */
    @Override
    public void saveWorldSpawn(Location location) {
        worldSpawn = location;
        setWorldSpawn();
    }

    /**
     * Retrieves the world spawn location.
     * 
     * @return The world spawn location, or null if none is set
     */
    @Override
    public Location getWorldSpawn() {
        return worldSpawn;
    }

    /**
     * Loads all location data from the storage backend.
     * This method should be called during plugin initialization to populate
     * the storage with existing data.
     */
    @Override
    public void loadAll() {
        loadLocationsFromSection("lastDeathLocations", deathLocations);
        loadLocationsFromSection("lastTeleportLocations", teleportLocations);
        loadLocationsFromSection("lastLocations", lastLocations);
        loadLocationsFromSection("homeLocations", homeLocations);
        loadWarpsFromSection("warpLocations");
        loadWorldSpawnLocation();
    }

    /**
     * Saves all location data to the storage backend.
     * This method should be called periodically and during plugin shutdown
     * to ensure data persistence.
     */
    @Override
    public void saveAll() {
        saveLocationsToSection("lastDeathLocations", deathLocations);  // Geändert für Konsistenz
        saveLocationsToSection("lastTeleportLocations", teleportLocations);  // Geändert für Konsistenz
        saveLocationsToSection("lastLocations", lastLocations);
        saveLocationsToSection("homeLocations", homeLocations);
        saveWorldSpawnLocation();
        saveWarpsToSection("warpLocations");
        
        // Save to file
        saveConfigFile();
    }

    /**
     * Closes the storage backend and releases any resources.
     * This method should be called during plugin shutdown to properly
     * clean up connections, file handles, or other resources.
     */
    @Override
    public void close() {
        saveAll();
    }
    
    /**
     * Saves the locations configuration to the file system.
     * Handles any IO exceptions that may occur during file writing.
     */
    private void saveConfigFile() {
        try {
            locationsConfig.save(locationsFile);
            logger.info("Location data saved successfully to " + locationsFile.getName());
        } catch (IOException e) {
            logger.severe("Could not save locations to " + locationsFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveLocationsToSection( String sectionName, Map<UUID, Location> locationMap) {
        // Clear existing section to prevent orphaned entries
        locationsConfig.set(sectionName, null);
        for (Map.Entry<UUID, Location> entry : locationMap.entrySet()) {
            locationsConfig.set(sectionName + "." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Saves warp location data to a configuration section.
     * Helper method specifically for saving string-based warp LocationManager.
     * 
     * <p>This method clears the existing section before saving to prevent
     * orphaned warp entries from remaining in the configuration.
     * 
     * @param sectionName The name of the config section for warps
     */
    private void saveWarpsToSection( String sectionName) {
        // Clear existing section to prevent orphaned entries
        locationsConfig.set(sectionName, null);
        for (Map.Entry<String, Location> entry : warpLocations.entrySet()) {
            locationsConfig.set(sectionName + "." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Saves the world spawn location to configuration.
     * Stores the current world spawn location for persistence.
     */
    private void saveWorldSpawnLocation() {
        locationsConfig.set("worldSpawnLocation", worldSpawn);
    }
    /**
     * Loads player location data from a configuration section.
     * Helper method to load UUID-based location maps from locationsConfig.
     * 
     * @param sectionName The name of the config section
     * @param locationMap The map to populate with loaded data
     */
    private void loadLocationsFromSection(String sectionName, Map<UUID, Location> locationMap) {
        ConfigurationSection section = locationsConfig.getConfigurationSection(sectionName);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(key);
                    Location location = (Location) locationsConfig.get(sectionName + "." + key);
                    if (location != null) {
                        locationMap.put(playerId, location);
                    }
                } catch (IllegalArgumentException e) {
                    // Skip invalid UUID entries
                }
            }
        }
    }

    /**
     * Loads warp location data from a configuration section.
     * Helper method specifically for loading string-based warp LocationManager.
     * 
     * @param sectionName The name of the config section for warps
     */
    private void loadWarpsFromSection(String sectionName) {
        ConfigurationSection section = locationsConfig.getConfigurationSection(sectionName);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Location location = (Location) locationsConfig.get(sectionName + "." + key);
                if (location != null) {
                    warpLocations.put(key, location);
                }
            }
        }
    }

    /**
     * Loads the world spawn location from configuration.
     */
    private void loadWorldSpawnLocation() {
        worldSpawn = (Location) locationsConfig.get("worldSpawnLocation");
        if (worldSpawn != null) {
            setWorldSpawn();
        }
    }
    
    /**
     * Applies the world spawn location to the actual world.
     * Sets the world's spawn point to the stored location.
     * 
     * <p>This method is called when:
     * <ul>
     * <li>Loading world spawn from config</li>
     * <li>Setting a new world spawn location</li>
     * </ul>
     */
    private void setWorldSpawn() {
        if (worldSpawn != null && worldSpawn.getWorld() != null) {
            worldSpawn.getWorld().setSpawnLocation(worldSpawn);
        }
    }
    
}
