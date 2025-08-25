package de.pitbully.pitbullyplugin.storage;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.utils.ConfigManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        
        // Load existing configuration or create empty one
        if (this.locationsFile.exists() && this.locationsFile.length() > 0) {
            this.locationsConfig = YamlConfiguration.loadConfiguration(this.locationsFile);
            if (PitbullyPlugin.getInstance() != null && 
                PitbullyPlugin.getInstance().getConfigManager() != null && 
                PitbullyPlugin.getInstance().getConfigManager().isDebugModeEnabled()) {
                logger.info("[DEBUG] Loaded existing locations.yml with " + this.locationsFile.length() + " bytes of data.");
            }
        } else {
            this.locationsConfig = new YamlConfiguration();
            // Initialize with default structure to prevent empty file
            initializeDefaultStructure();
            if (PitbullyPlugin.getInstance() != null && 
                PitbullyPlugin.getInstance().getConfigManager() != null && 
                PitbullyPlugin.getInstance().getConfigManager().isDebugModeEnabled()) {
                logger.info("[DEBUG] Created new locations.yml configuration structure.");
            }
        }
        
        // WICHTIG: Lade existierende Daten sofort in die Maps
        if (this.locationsFile.exists() && this.locationsFile.length() > 0) {
            loadAll();
            if (PitbullyPlugin.getInstance() != null && 
                PitbullyPlugin.getInstance().getConfigManager() != null && 
                PitbullyPlugin.getInstance().getConfigManager().isDebugModeEnabled()) {
                logger.info("[DEBUG] Loaded existing location data from file.");
            }
        }
        
        if (PitbullyPlugin.getInstance() != null && 
            PitbullyPlugin.getInstance().getConfigManager() != null && 
            PitbullyPlugin.getInstance().getConfigManager().isDebugModeEnabled()) {
            logger.info("[DEBUG] FileLocationStorage initialized with locations file: " + this.locationsFile.getAbsolutePath());
        }
    }
    
    /**
     * Initializes the default structure for locations.yml to prevent completely empty files.
     */
    private void initializeDefaultStructure() {
        locationsConfig.set("worldSpawnLocation", null);
        locationsConfig.set("homeLocations", new HashMap<String, Object>());
        locationsConfig.set("warpLocations", new HashMap<String, Object>());
        locationsConfig.set("lastDeathLocations", new HashMap<String, Object>());
        locationsConfig.set("lastTeleportLocations", new HashMap<String, Object>());
        locationsConfig.set("lastLocations", new HashMap<String, Object>());
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
     * Checks if a player has a last known location.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a last location, false otherwise
     */
    @Override
    public boolean checkLastLocation(UUID playerId) {
        return lastLocations.containsKey(playerId);
    }

    /**
     * Checks if a world spawn location is set.
     * 
     * @return true if world spawn is set, false otherwise
     */
    @Override
    public boolean checkWorldSpawnLocation() {
        return worldSpawn != null;
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
     * Creates a backup if enabled in configuration.
     * Handles any IO exceptions that may occur during file writing.
     */
    private void saveConfigFile() {
        try {
            // Create backup if enabled
            createBackupIfEnabled();
            
            locationsConfig.save(locationsFile);
            
            PitbullyPlugin plugin = PitbullyPlugin.getInstance();
            ConfigManager configManager = plugin != null ? plugin.getConfigManager() : null;
            
            if (configManager != null && configManager.isDebugModeEnabled()) {
                logger.info("[DEBUG] Location data saved successfully to " + locationsFile.getName());
            }
        } catch (IOException e) {
            logger.severe("Could not save locations to " + locationsFile.getAbsolutePath() + ": " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Exception while saving locations file", e);
        }
    }
    
    /**
     * Creates a backup of the locations file if backup creation is enabled.
     * Backup files are named with timestamp format: locations_YYYY-MM-dd_HH-mm-ss.yml.bak
     */
    private void createBackupIfEnabled() {
        try {
            PitbullyPlugin plugin = PitbullyPlugin.getInstance();
            ConfigManager configManager = plugin != null ? plugin.getConfigManager() : null;
            
            // If config manager is not available, assume backups are enabled (safe default)
            boolean createBackups = configManager == null || configManager.isCreateBackupsEnabled();
            
            if (!createBackups) {
                return;
            }
            
            // Only create backup if the file exists and is not empty
            if (!locationsFile.exists() || locationsFile.length() == 0) {
                return;
            }
            
            // Create backup directory
            File backupDir = new File(locationsFile.getParent(), "backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            // Generate backup filename with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String backupFileName = "locations_" + timestamp + ".yml.bak";
            File backupFile = new File(backupDir, backupFileName);
            
            // Copy file to backup location
            Files.copy(locationsFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            if (configManager != null) {
                configManager.debug("Created backup: " + backupFile.getName());
            }
            
        } catch (IOException e) {
            logger.warning("Failed to create backup of locations file: " + e.getMessage());
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

    /** Checks if a player's last death location is set. */
    @Override
    public boolean hasLastDeathLocation(UUID uniqueId) {
        return deathLocations.containsKey(uniqueId);
    }

    /** Checks if a player's last teleport location is set. */
    @Override
    public boolean hasLastTeleportLocation(UUID uniqueId) {
        return teleportLocations.containsKey(uniqueId);
    }

    /** Retrieves a player's last teleport location. */
    @Override
    public Location getLastTeleportLocation(UUID uniqueId) {
        return teleportLocations.get(uniqueId);
    }
}
