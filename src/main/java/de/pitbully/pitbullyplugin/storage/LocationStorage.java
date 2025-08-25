package de.pitbully.pitbullyplugin.storage;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;

/**
 * Interface for location data storage implementations.
 * 
 * <p>This interface defines the contract for storing and retrieving various types
 * of location data in the PitbullyPlugin. Implementations can use different storage
 * backends such as files, databases, or other persistence mechanisms.
 * 
 * <p>The interface supports the following location types:
 * <ul>
 * <li>Death locations - where players died (for /back command)</li>
 * <li>Teleport locations - locations before teleportation (for /back command)</li>
 * <li>Last locations - general last known locations (for /back command)</li>
 * <li>Home locations - player-set home points (/home, /sethome)</li>
 * <li>Warp locations - server-wide warp points (/warp, /setwarp)</li>
 * <li>World spawn - the world's spawn location (/setspawn)</li>
 * </ul>
 * 
 * @author Pitbully01
 * @since 1.5.1
 */
public interface LocationStorage {

    // Death locations
    
    /**
     * Saves a player's death location.
     * 
     * @param playerId The UUID of the player who died
     * @param location The location where the player died
     */
    void saveDeathLocation(UUID playerId, Location location);
    
    /**
     * Retrieves a player's last death location.
     * 
     * @param playerId The UUID of the player
     * @return The player's last death location, or null if none exists
     */
    Location getDeathLocation(UUID playerId);
    
    /**
     * Retrieves all stored death LocationManager.
     * 
     * @return A map of player UUIDs to their death locations
     */
    Map<UUID, Location> getAllDeathLocations();

    // Teleport locations
    
    /**
     * Saves a player's location before teleportation.
     * 
     * @param playerId The UUID of the player
     * @param location The location before the teleportation occurred
     */
    void saveTeleportLocation(UUID playerId, Location location);
    
    /**
     * Retrieves a player's last teleport location.
     * 
     * @param playerId The UUID of the player
     * @return The player's last teleport location, or null if none exists
     */
    Location getTeleportLocation(UUID playerId);
    
    /**
     * Retrieves all stored teleport LocationManager.
     * 
     * @return A map of player UUIDs to their teleport locations
     */
    Map<UUID, Location> getAllTeleportLocations();
    
    // Last locations
    
    /**
     * Saves a player's general last location.
     * This is typically used as a fallback for the /back command.
     * 
     * @param playerId The UUID of the player
     * @param location The location to save as the last location
     */
    void saveLastLocation(UUID playerId, Location location);
    
    /**
     * Retrieves a player's last known location.
     * 
     * @param playerId The UUID of the player
     * @return The player's last known location, or null if none exists
     */
    Location getLastLocation(UUID playerId);
    
    /**
     * Retrieves all stored last LocationManager.
     * 
     * @return A map of player UUIDs to their last locations
     */
    Map<UUID, Location> getAllLastLocations();
    
    // Home locations
    
    /**
     * Saves a player's home location.
     * 
     * @param playerId The UUID of the player
     * @param location The location to set as the player's home
     */
    void saveHomeLocation(UUID playerId, Location location);
    
    /**
     * Retrieves a player's home location.
     * 
     * @param playerId The UUID of the player
     * @return The player's home location, or null if none is set
     */
    Location getHomeLocation(UUID playerId);
    
    /**
     * Checks if a player has a home location set.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a home location, false otherwise
     */
    boolean hasHomeLocation(UUID playerId);
    
    /**
     * Removes a player's home location.
     * 
     * @param playerId The UUID of the player whose home to delete
     */
    void deleteHomeLocation(UUID playerId);
    
    /**
     * Retrieves all stored home LocationManager.
     * 
     * @return A map of player UUIDs to their home locations
     */
    Map<UUID, Location> getAllHomeLocations();
    
    // Warp locations
    
    /**
     * Saves a warp location with the given name.
     * 
     * @param warpName The name of the warp
     * @param location The location for the warp
     */
    void saveWarpLocation(String warpName, Location location);
    
    /**
     * Retrieves a warp location by name.
     * 
     * @param warpName The name of the warp
     * @return The warp location, or null if the warp doesn't exist
     */
    Location getWarpLocation(String warpName);
    
    /**
     * Checks if a warp with the given name exists.
     * 
     * @param warpName The name of the warp to check
     * @return true if the warp exists, false otherwise
     */
    boolean hasWarpLocation(String warpName);
    
    /**
     * Removes a warp location.
     * 
     * @param warpName The name of the warp to delete
     */
    void deleteWarpLocation(String warpName);
    
    /**
     * Retrieves all stored warp LocationManager.
     * 
     * @return A map of warp names to their locations
     */
    Map<String, Location> getAllWarpLocations();
    
    // World spawn
    
    /**
     * Saves the world spawn location.
     * 
     * @param location The location to set as world spawn
     */
    void saveWorldSpawn(Location location);
    
    /**
     * Retrieves the world spawn location.
     * 
     * @return The world spawn location, or null if none is set
     */
    Location getWorldSpawn();
    
    // Convenience methods for compatibility with Locations class
    
    /**
     * Checks if a player has a last known location.
     * 
     * @param playerId The UUID of the player
     * @return true if the player has a last location, false otherwise
     */
    default boolean checkLastLocation(UUID playerId) {
        return getLastLocation(playerId) != null;
    }
    
    /**
     * Checks if a world spawn location is set.
     * 
     * @return true if world spawn is set, false otherwise
     */
    default boolean checkWorldSpawnLocation() {
        return getWorldSpawn() != null;
    }
    
    // Lifecycle methods
    
    /**
     * Loads all location data from the storage backend.
     * This method should be called during plugin initialization to populate
     * the storage with existing data.
     */
    void loadAll();
    
    /**
     * Saves all location data to the storage backend.
     * This method should be called periodically and during plugin shutdown
     * to ensure data persistence.
     */
    void saveAll();
    
    /**
     * Closes the storage backend and releases any resources.
     * This method should be called during plugin shutdown to properly
     * clean up connections, file handles, or other resources.
     */
    void close();

    /**
     * Checks if a player has a last known death location.
     * @param uniqueId The UUID of the player
     * @return true if the player has a last death location, false otherwise
     */
    boolean hasLastDeathLocation(UUID uniqueId);

    /**
     * Checks if a player has a last known teleport location.
     * @param uniqueId The UUID of the player
     * @return true if the player has a last teleport location, false otherwise
     */
    boolean hasLastTeleportLocation(UUID uniqueId);

    /**
     * Retrieves a player's last teleport location.
     * @param uniqueId The UUID of the player
     * @return The player's last teleport location, or null if none exists
     */
    Location getLastTeleportLocation(UUID uniqueId);
}
