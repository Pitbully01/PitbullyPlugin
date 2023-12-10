package de.pitbully.pitbullyplugin.utils;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Locations {

    private static HashMap<UUID, Location> lastDeathLocations = new HashMap<>();
    private static HashMap<UUID, Location> lastTeleportLocations = new HashMap<>();
    private static HashMap<UUID, Location> lastLocations = new HashMap<>();
    private static HashMap<UUID, Location> homeLocations = new HashMap<>();
    private static HashMap<String, Location> warpLocations = new HashMap<>();


    public static void updateLastDeathLocations(UUID playerId, Location location) {
        lastDeathLocations.put(playerId, location);
        updateLastLocations(playerId, location);
    }

    public static Location getLastDeathLocation(UUID playerId) {
        return lastDeathLocations.get(playerId);
    }

    public static void updateLastTeleportLocations(UUID playerId, Location location) {
        lastTeleportLocations.put(playerId, location);
        updateLastLocations(playerId, location);
    }

    public static Location getLastTeleportLocations(UUID playerId) {
        return lastTeleportLocations.get(playerId);
    }

    public static void updateLastLocations(UUID playerId, Location location) {
        lastLocations.put(playerId, location);
    }

    public static Location getLastLocation(UUID playerId) {
        return lastLocations.get(playerId);
    }

    public static boolean checkLastLocation(UUID playerId) {
        return lastLocations.containsKey(playerId);
    }

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

    public static void updateWarpLocation(String warp, Location location) {
        warpLocations.put(warp, location);
    }

    public static Location getWarpLocation(String warp) {
        return warpLocations.get(warp);
    }

    public static HashMap<String, Location> getWarpHashMap() {
        return warpLocations;
    }

    public static boolean checkWarpLocation(String warp) {
        return warpLocations.containsKey(warp);
    }

    public static void deleteWarpLocation(String warp) {

        warpLocations.remove(warp);

    }

    public static void loadFromConfig(FileConfiguration config) {
        // Load lastDeathLocations
        ConfigurationSection lastDeathSection = config.getConfigurationSection("lastDeathLocations");
        if (lastDeathSection != null) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("lastDeathLocations")).getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                Location location = (Location) config.get("lastDeathLocations." + key);
                lastDeathLocations.put(playerId, location);
            }
        }

        // Load lastTeleportLocations
        ConfigurationSection lastTeleportSection = config.getConfigurationSection("lastTeleportLocations");
        if (lastTeleportSection != null) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("lastTeleportLocations")).getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                Location location = (Location) config.get("lastTeleportLocations." + key);
                lastTeleportLocations.put(playerId, location);
            }
        }

        // Load lastLocations
        ConfigurationSection lastSection = config.getConfigurationSection("lastLocations");
        if (lastSection != null) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("lastLocations")).getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                Location location = (Location) config.get("lastLocations." + key);
                lastLocations.put(playerId, location);
            }
        }

        // Load homeLocations
        ConfigurationSection homeSection = config.getConfigurationSection("homeLocations");
        if (homeSection != null) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("homeLocations")).getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                Location location = (Location) config.get("homeLocations." + key);
                homeLocations.put(playerId, location);
            }
        }

        // Load WarpLocations
        ConfigurationSection warpSection = config.getConfigurationSection("warpLocations");
        if (warpSection != null) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("warpLocations")).getKeys(false)) {
                Location location = (Location) config.get("warpLocations." + key);
                warpLocations.put(key, location);
            }
        }

    }

    public static void saveToConfig(FileConfiguration config) {
        // Save lastDeathLocations
        for (UUID playerId : lastDeathLocations.keySet()) {
            config.set("lastDeathLocations." + playerId, lastDeathLocations.get(playerId));
        }

        // Save lastTeleportLocations
        for (UUID playerId : lastTeleportLocations.keySet()) {
            config.set("lastTeleportLocations." + playerId, lastTeleportLocations.get(playerId));
        }

        // Save lastLocations
        for (UUID playerId : lastLocations.keySet()) {
            config.set("lastLocations." + playerId, lastLocations.get(playerId));
        }

        // Save homeLocations
        for (UUID playerId : homeLocations.keySet()) {
            config.set("homeLocations." + playerId, homeLocations.get(playerId));
        }

        // Save warpLocations
        for (String warpName : warpLocations.keySet()) {
            config.set("warpLocations." + warpName, warpLocations.get(warpName));
        }
    }


}
