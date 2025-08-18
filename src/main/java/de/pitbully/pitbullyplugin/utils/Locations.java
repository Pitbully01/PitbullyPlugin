package de.pitbully.pitbullyplugin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Utility class for managing player locations including homes, warps, 
 * death locations, and teleport history.
 * 
 * <p>This class serves as the central data management system for all
 * location-based features in the plugin. It handles:
 * <ul>
 * <li>Player home locations (/home, /sethome, /delhome)</li>
 * <li>Server warp points (/warp, /setwarp, /delwarp)</li>
 * <li>Death location tracking (for /back command)</li>
 * <li>Teleportation history (for /back command)</li>
 * <li>World spawn location (/setspawn)</li>
 * </ul>
 * 
 * <p>All data is persisted to the plugin's configuration file and loaded
 * automatically when the plugin starts.
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class Locations {
    
    private static final Map<UUID, Location> lastDeathLocations = new HashMap<>();
    private static final Map<UUID, Location> lastTeleportLocations = new HashMap<>();
    private static final Map<UUID, Location> lastLocations = new HashMap<>();
    private static final Map<UUID, Location> homeLocations = new HashMap<>();
    private static final Map<String, Location> warpLocations = new HashMap<>();
    private static Location worldSpawnLocation;

    // Death Location Methods
    
    /**
     * Updates the last death location for a player.
     * Also updates the general last location for /back command functionality.
     * 
     * @param playerId The UUID of the player
     * @param location The death location to store
     */
    public static void updateLastDeathLocations(UUID playerId, Location location) {
        lastDeathLocations.put(playerId, location);
        updateLastLocations(playerId, location);
    }

    /**
     * Retrieves the last death location for a player.
     * 
     * @param playerId The UUID of the player
     * @return The player's last death location, or null if none exists
     */
    public static Location getLastDeathLocation(UUID playerId) {
        return lastDeathLocations.get(playerId);
    }

    // Teleport Location Methods
    
    /**
     * Updates the last teleport location for a player.
     * Also updates the general last location for /back command functionality.
     * 
     * @param playerId The UUID of the player
     * @param location The location before teleportation
     */
    public static void updateLastTeleportLocations(UUID playerId, Location location) {
        lastTeleportLocations.put(playerId, location);
        updateLastLocations(playerId, location);
    }

    /**
     * Retrieves the last teleport location for a player.
     * 
     * @param playerId The UUID of the player
     * @return The player's last teleport location, or null if none exists
     */
    public static Location getLastTeleportLocations(UUID playerId) {
        return lastTeleportLocations.get(playerId);
    }

    // Last Location Methods
    
    /**
     * Updates the general last location for a player.
     * This is used by the /back command to determine where to teleport.
     * 
     * @param playerId The UUID of the player
     * @param location The location to store as the last location
     */
    public static void updateLastLocations(UUID playerId, Location location) {
        lastLocations.put(playerId, location);
    }

    /**
     * Retrieves the last known location for a player.
     * Used by the /back command for teleportation.
     * 
     * @param playerId The UUID of the player
     * @return The player's last known location, or null if none exists
     */
    public static Location getLastLocation(UUID playerId) {
        return lastLocations.get(playerId);
    }

    /**
     * Checks if a player has a last known location.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a last location, false otherwise
     */
    public static boolean checkLastLocation(UUID playerId) {
        return lastLocations.containsKey(playerId);
    }

    // Home Location Methods
    
    /**
     * Sets or updates a player's home location.
     * 
     * @param playerId The UUID of the player
     * @param location The location to set as the player's home
     */
    public static void updateHomeLocation(UUID playerId, Location location) {
        homeLocations.put(playerId, location);
    }

    /**
     * Retrieves a player's home location.
     * 
     * @param playerId The UUID of the player
     * @return The player's home location, or null if none is set
     */
    public static Location getHomeLocation(UUID playerId) {
        return homeLocations.get(playerId);
    }

    /**
     * Checks if a player has a home location set.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a home set, false otherwise
     */
    public static boolean checkHomeLocation(UUID playerId) {
        return homeLocations.containsKey(playerId);
    }

    /**
     * Removes a player's home location.
     * 
     * @param playerId The UUID of the player whose home to delete
     */
    public static void deleteHomeLocation(UUID playerId) {
        homeLocations.remove(playerId);
    }

    // Warp Location Methods
    
    /**
     * Creates or updates a server warp location.
     * 
     * @param warp The name of the warp
     * @param location The location for the warp
     */
    public static void updateWarpLocation(String warp, Location location) {
        warpLocations.put(warp, location);
    }

    /**
     * Retrieves a warp location by name.
     * 
     * @param warp The name of the warp
     * @return The warp location, or null if the warp doesn't exist
     */
    public static Location getWarpLocation(String warp) {
        return warpLocations.get(warp);
    }

    /**
     * Gets a copy of all warp locations.
     * Used for tab completion and warp listing.
     * 
     * @return A new HashMap containing all warp locations
     */
    public static Map<String, Location> getWarpHashMap() {
        return new HashMap<>(warpLocations);
    }

    /**
     * Checks if a warp exists.
     * 
     * @param warp The name of the warp to check
     * @return true if the warp exists, false otherwise
     */
    public static boolean checkWarpLocation(String warp) {
        return warpLocations.containsKey(warp);
    }

    /**
     * Removes a warp location.
     * 
     * @param warp The name of the warp to delete
     */
    public static void deleteWarpLocation(String warp) {
        warpLocations.remove(warp);
    }

    // World Spawn Location Methods
    
    /**
     * Sets the world spawn location and immediately applies it to the world.
     * 
     * @param location The location to set as world spawn
     */
    public static void updateWorldSpawnLocation(Location location) {
        worldSpawnLocation = location;
        setWorldSpawn();
    }

    /**
     * Retrieves the current world spawn location.
     * 
     * @return The world spawn location, or null if none is set
     */
    public static Location getWorldSpawnLocation() {
        return worldSpawnLocation;
    }

    /**
     * Checks if a world spawn location is set.
     * 
     * @return true if world spawn is set, false otherwise
     */
    public static boolean checkWorldSpawnLocation() {
        return worldSpawnLocation != null;
    }

    /**
     * Loads location data from the configuration file.
     * Populates all location maps with data from the config.
     * 
     * <p>This method is called during plugin startup to restore
     * all previously saved location data including homes, warps,
     * death locations, and teleport history.
     * 
     * @param config The configuration to load from
     */
    public static void loadFromConfig(FileConfiguration config) {
        loadLocationsFromSection(config, "lastDeathLocations", lastDeathLocations);
        loadLocationsFromSection(config, "lastTeleportLocations", lastTeleportLocations);
        loadLocationsFromSection(config, "lastLocations", lastLocations);
        loadLocationsFromSection(config, "homeLocations", homeLocations);
        loadWorldSpawnFromConfig(config);
        loadWarpsFromSection(config, "warpLocations");
    }

    /**
     * Saves location data to the configuration file.
     * Persists all current location data to the config for permanent storage.
     * 
     * <p>This method is called whenever location data changes or
     * when the plugin shuts down to ensure data persistence.
     * 
     * @param config The configuration to save to
     */
    public static void saveToConfig(FileConfiguration config) {
        saveLocationsToSection(config, "lastDeathLocations", lastDeathLocations);
        saveLocationsToSection(config, "lastTeleportLocations", lastTeleportLocations);
        saveLocationsToSection(config, "lastLocations", lastLocations);
        saveLocationsToSection(config, "homeLocations", homeLocations);
        saveWorldSpawnLocation(config);
        saveWarpsToSection(config, "warpLocations");
    }

    /**
     * Loads player location data from a configuration section.
     * Helper method to load UUID-based location maps from config.
     * 
     * <p>This method safely handles invalid UUIDs by skipping them,
     * preventing configuration corruption from breaking the plugin.
     * 
     * @param config The configuration to load from
     * @param sectionName The name of the config section
     * @param locationMap The map to populate with loaded data
     */
    private static void loadLocationsFromSection(FileConfiguration config, String sectionName, 
                                               Map<UUID, Location> locationMap) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(key);
                    Location location = (Location) config.get(sectionName + "." + key);
                    if (location != null) {
                        locationMap.put(playerId, location);
                    }
                } catch (IllegalArgumentException e) {
                    // Skip invalid UUID entries - this prevents errors from corrupted config data
                }
            }
        }
    }

    /**
     * Loads warp location data from a configuration section.
     * Helper method specifically for loading string-based warp locations.
     * 
     * @param config The configuration to load from
     * @param sectionName The name of the config section containing warps
     */
    private static void loadWarpsFromSection(FileConfiguration config, String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Location location = (Location) config.get(sectionName + "." + key);
                if (location != null) {
                    warpLocations.put(key, location);
                }
            }
        }
    }

    /**
     * Loads the world spawn location from configuration.
     * Also applies the loaded spawn location to the world if valid.
     * 
     * @param config The configuration to load from
     */
    private static void loadWorldSpawnFromConfig(FileConfiguration config) {
        worldSpawnLocation = (Location) config.get("worldSpawnLocation");
        if (worldSpawnLocation != null) {
            setWorldSpawn();
        }
    }

    /**
     * Saves player location data to a configuration section.
     * Helper method to save UUID-based location maps to config.
     * 
     * <p>This method clears the existing section before saving to prevent
     * orphaned entries from remaining in the configuration.
     * 
     * @param config The configuration to save to
     * @param sectionName The name of the config section
     * @param locationMap The map containing location data to save
     */
    private static void saveLocationsToSection(FileConfiguration config, String sectionName, Map<UUID, Location> locationMap) {
        // Clear existing section to prevent orphaned entries
        config.set(sectionName, null);
        for (Map.Entry<UUID, Location> entry : locationMap.entrySet()) {
            config.set(sectionName + "." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Saves warp location data to a configuration section.
     * Helper method specifically for saving string-based warp locations.
     * 
     * <p>This method clears the existing section before saving to prevent
     * orphaned warp entries from remaining in the configuration.
     * 
     * @param config The configuration to save to
     * @param sectionName The name of the config section for warps
     */
    private static void saveWarpsToSection(FileConfiguration config, String sectionName) {
        // Clear existing section to prevent orphaned entries
        config.set(sectionName, null);
        for (Map.Entry<String, Location> entry : warpLocations.entrySet()) {
            config.set(sectionName + "." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Saves the world spawn location to configuration.
     * Stores the current world spawn location for persistence.
     * 
     * @param config The configuration to save to
     */
    private static void saveWorldSpawnLocation(FileConfiguration config) {
        config.set("worldSpawnLocation", worldSpawnLocation);
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
    private static void setWorldSpawn() {
        if (worldSpawnLocation != null && worldSpawnLocation.getWorld() != null) {
            worldSpawnLocation.getWorld().setSpawnLocation(worldSpawnLocation);
        }
    }
}
