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
 */
public class Locations {
    
    private static final Map<UUID, Location> lastDeathLocations = new HashMap<>();
    private static final Map<UUID, Location> lastTeleportLocations = new HashMap<>();
    private static final Map<UUID, Location> lastLocations = new HashMap<>();
    private static final Map<UUID, Location> homeLocations = new HashMap<>();
    private static final Map<String, Location> warpLocations = new HashMap<>();

    // Death Location Methods
    public static void updateLastDeathLocations(UUID playerId, Location location) {
        lastDeathLocations.put(playerId, location);
        updateLastLocations(playerId, location);
    }

    public static Location getLastDeathLocation(UUID playerId) {
        return lastDeathLocations.get(playerId);
    }

    // Teleport Location Methods
    public static void updateLastTeleportLocations(UUID playerId, Location location) {
        lastTeleportLocations.put(playerId, location);
        updateLastLocations(playerId, location);
    }

    public static Location getLastTeleportLocations(UUID playerId) {
        return lastTeleportLocations.get(playerId);
    }

    // Last Location Methods
    public static void updateLastLocations(UUID playerId, Location location) {
        lastLocations.put(playerId, location);
    }

    public static Location getLastLocation(UUID playerId) {
        return lastLocations.get(playerId);
    }

    public static boolean checkLastLocation(UUID playerId) {
        return lastLocations.containsKey(playerId);
    }

    // Home Location Methods
    public static void updateHomeLocation(UUID playerId, Location location) {
        homeLocations.put(playerId, location);
    }

    public static Location getHomeLocation(UUID playerId) {
        return homeLocations.get(playerId);
    }

    public static boolean checkHomeLocation(UUID playerId) {
        return homeLocations.containsKey(playerId);
    }

    public static void deleteHomeLocation(UUID playerId) {
        homeLocations.remove(playerId);
    }

    // Warp Location Methods
    public static void updateWarpLocation(String warp, Location location) {
        warpLocations.put(warp, location);
    }

    public static Location getWarpLocation(String warp) {
        return warpLocations.get(warp);
    }

    public static Map<String, Location> getWarpHashMap() {
        return new HashMap<>(warpLocations);
    }

    public static boolean checkWarpLocation(String warp) {
        return warpLocations.containsKey(warp);
    }

    public static void deleteWarpLocation(String warp) {
        warpLocations.remove(warp);
    }

    /**
     * Loads location data from the configuration file.
     * 
     * @param config The configuration to load from
     */
    public static void loadFromConfig(FileConfiguration config) {
        loadLocationsFromSection(config, "lastDeathLocations", lastDeathLocations);
        loadLocationsFromSection(config, "lastTeleportLocations", lastTeleportLocations);
        loadLocationsFromSection(config, "lastLocations", lastLocations);
        loadLocationsFromSection(config, "homeLocations", homeLocations);
        loadWarpsFromSection(config, "warpLocations");
    }

    /**
     * Saves location data to the configuration file.
     * 
     * @param config The configuration to save to
     */
    public static void saveToConfig(FileConfiguration config) {
        saveLocationsToSection(config, "lastDeathLocations", lastDeathLocations);
        saveLocationsToSection(config, "lastTeleportLocations", lastTeleportLocations);
        saveLocationsToSection(config, "lastLocations", lastLocations);
        saveLocationsToSection(config, "homeLocations", homeLocations);
        saveWarpsToSection(config, "warpLocations");
    }

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
                    // Skip invalid UUID entries
                }
            }
        }
    }

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

    private static void saveLocationsToSection(FileConfiguration config, String sectionName, 
                                             Map<UUID, Location> locationMap) {
        for (Map.Entry<UUID, Location> entry : locationMap.entrySet()) {
            config.set(sectionName + "." + entry.getKey(), entry.getValue());
        }
    }

    private static void saveWarpsToSection(FileConfiguration config, String sectionName) {
        for (Map.Entry<String, Location> entry : warpLocations.entrySet()) {
            config.set(sectionName + "." + entry.getKey(), entry.getValue());
        }
    }
}
