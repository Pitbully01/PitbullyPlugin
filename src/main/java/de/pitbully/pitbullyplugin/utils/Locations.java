package de.pitbully.pitbullyplugin.utils;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Locations {

    private static HashMap<UUID, Location> lastDeathLocations = new HashMap<>();
    private static HashMap<UUID, Location> lastTeleportLocations = new HashMap<>();
    private static HashMap<UUID, Location> lastLocations = new HashMap<>();
    private static HashMap<UUID, Location> homeLocations = new HashMap<>();


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

    public static void loadFromConfig(FileConfiguration config) {
        // Load lastDeathLocations
        for (String key : Objects.requireNonNull(config.getConfigurationSection("lastDeathLocations")).getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Location location = (Location) config.get("lastDeathLocations." + key);
            lastDeathLocations.put(playerId, location);
        }

        // Load lastTeleportLocations
        for (String key : Objects.requireNonNull(config.getConfigurationSection("lastTeleportLocations")).getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Location location = (Location) config.get("lastTeleportLocations." + key);
            lastTeleportLocations.put(playerId, location);
        }

        // Load lastLocations
        for (String key : Objects.requireNonNull(config.getConfigurationSection("lastLocations")).getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Location location = (Location) config.get("lastLocations." + key);
            lastLocations.put(playerId, location);
        }

        // Load homeLocations
        for (String key : Objects.requireNonNull(config.getConfigurationSection("homeLocations")).getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Location location = (Location) config.get("homeLocations." + key);
            homeLocations.put(playerId, location);
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
    }


}
