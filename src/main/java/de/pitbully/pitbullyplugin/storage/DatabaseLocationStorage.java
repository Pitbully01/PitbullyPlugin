package de.pitbully.pitbullyplugin.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import de.pitbully.pitbullyplugin.utils.PlayerData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Database-based implementation of LocationStorage.
 * 
 * <p>This implementation stores all location data in a SQL database using HikariCP
 * connection pooling for optimal performance. Supports MySQL, MariaDB, PostgreSQL, and SQLite.
 * 
 * @author Pitbully01
 * @since 1.5.2
 */
public class DatabaseLocationStorage implements LocationStorage {
    
    private final DatabaseConfig config;
    private final Logger logger;
    private HikariDataSource dataSource;
    
    // Table names
    private static final String TABLE_PLAYER_LOCATIONS = "pitbully_player_locations";
    private static final String TABLE_WARP_LOCATIONS = "pitbully_warp_locations";
    private static final String TABLE_WORLD_SPAWN = "pitbully_world_spawn";
    
    // Location types
    private static final String TYPE_DEATH = "death";
    private static final String TYPE_TELEPORT = "teleport";
    private static final String TYPE_LAST = "last";
    private static final String TYPE_HOME = "home";
    
    public DatabaseLocationStorage(DatabaseConfig config, Logger logger) {
        this.config = config;
        this.logger = logger;
        initializeDatabase();
    }
    
    /**
     * Initializes the database connection and creates necessary tables.
     */
    private void initializeDatabase() {
        try {
            // Load the database driver
            Class.forName(config.getType().getDriverClass());
            
            // Configure HikariCP
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.buildJdbcUrl());
            
            if (config.getType() != DatabaseConfig.DatabaseType.SQLITE) {
                hikariConfig.setUsername(config.getUsername());
                hikariConfig.setPassword(config.getPassword());
            }
            
            hikariConfig.setMaximumPoolSize(config.getMaxConnections());
            hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
            hikariConfig.setMaxLifetime(config.getMaxLifetime());
            hikariConfig.setPoolName("PitbullyPlugin-Pool");
            
            // Additional settings
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            this.dataSource = new HikariDataSource(hikariConfig);
            
            logger.info("Database connection established successfully: " + config.getType().getName());
            
            // Create tables if they don't exist
            createTables();
            
        } catch (ClassNotFoundException e) {
            logger.severe("Database driver not found for " + config.getType().getName() + ": " + e.getMessage());
            throw new RuntimeException("Failed to load database driver", e);
        } catch (SQLException e) {
            logger.severe("Failed to initialize database connection: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Creates the necessary database tables if they don't exist.
     */
    private void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Create player locations table
            String createPlayerLocationsTable = getCreatePlayerLocationsTableSQL();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createPlayerLocationsTable);
            }
            
            // Create warp locations table
            String createWarpLocationsTable = getCreateWarpLocationsTableSQL();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createWarpLocationsTable);
            }
            
            // Create world spawn table
            String createWorldSpawnTable = getCreateWorldSpawnTableSQL();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createWorldSpawnTable);
            }
            
            logger.info("Database tables created/verified successfully");
        }
    }
    
    /**
     * Gets the SQL for creating the player locations table based on database type.
     */
    private String getCreatePlayerLocationsTableSQL() {
        switch (config.getType()) {
            case SQLITE:
                return "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYER_LOCATIONS + " (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "player_uuid TEXT NOT NULL, " +
                       "location_type TEXT NOT NULL, " +
                       "world_name TEXT NOT NULL, " +
                       "x REAL NOT NULL, " +
                       "y REAL NOT NULL, " +
                       "z REAL NOT NULL, " +
                       "yaw REAL NOT NULL, " +
                       "pitch REAL NOT NULL, " +
                       "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                       "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                       "UNIQUE(player_uuid, location_type)" +
                       ")";
            default:
                return "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYER_LOCATIONS + " (" +
                       "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                       "player_uuid VARCHAR(36) NOT NULL, " +
                       "location_type VARCHAR(20) NOT NULL, " +
                       "world_name VARCHAR(255) NOT NULL, " +
                       "x DOUBLE NOT NULL, " +
                       "y DOUBLE NOT NULL, " +
                       "z DOUBLE NOT NULL, " +
                       "yaw FLOAT NOT NULL, " +
                       "pitch FLOAT NOT NULL, " +
                       "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                       "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                       "UNIQUE KEY unique_player_location (player_uuid, location_type), " +
                       "INDEX idx_player_uuid (player_uuid), " +
                       "INDEX idx_location_type (location_type)" +
                       ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        }
    }
    
    /**
     * Gets the SQL for creating the warp locations table based on database type.
     */
    private String getCreateWarpLocationsTableSQL() {
        switch (config.getType()) {
            case SQLITE:
                return "CREATE TABLE IF NOT EXISTS " + TABLE_WARP_LOCATIONS + " (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "warp_name TEXT NOT NULL UNIQUE, " +
                       "world_name TEXT NOT NULL, " +
                       "x REAL NOT NULL, " +
                       "y REAL NOT NULL, " +
                       "z REAL NOT NULL, " +
                       "yaw REAL NOT NULL, " +
                       "pitch REAL NOT NULL, " +
                       "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                       "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                       ")";
            default:
                return "CREATE TABLE IF NOT EXISTS " + TABLE_WARP_LOCATIONS + " (" +
                       "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                       "warp_name VARCHAR(255) NOT NULL UNIQUE, " +
                       "world_name VARCHAR(255) NOT NULL, " +
                       "x DOUBLE NOT NULL, " +
                       "y DOUBLE NOT NULL, " +
                       "z DOUBLE NOT NULL, " +
                       "yaw FLOAT NOT NULL, " +
                       "pitch FLOAT NOT NULL, " +
                       "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                       "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                       "INDEX idx_warp_name (warp_name)" +
                       ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        }
    }
    
    /**
     * Gets the SQL for creating the world spawn table based on database type.
     */
    private String getCreateWorldSpawnTableSQL() {
        switch (config.getType()) {
            case SQLITE:
                return "CREATE TABLE IF NOT EXISTS " + TABLE_WORLD_SPAWN + " (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "world_name TEXT NOT NULL UNIQUE, " +
                       "x REAL NOT NULL, " +
                       "y REAL NOT NULL, " +
                       "z REAL NOT NULL, " +
                       "yaw REAL NOT NULL, " +
                       "pitch REAL NOT NULL, " +
                       "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                       "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                       ")";
            default:
                return "CREATE TABLE IF NOT EXISTS " + TABLE_WORLD_SPAWN + " (" +
                       "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                       "world_name VARCHAR(255) NOT NULL UNIQUE, " +
                       "x DOUBLE NOT NULL, " +
                       "y DOUBLE NOT NULL, " +
                       "z DOUBLE NOT NULL, " +
                       "yaw FLOAT NOT NULL, " +
                       "pitch FLOAT NOT NULL, " +
                       "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                       "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                       "INDEX idx_world_name (world_name)" +
                       ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        }
    }
    
    // Death locations
    @Override
    public void saveDeathLocation(UUID playerId, Location location) {
        savePlayerLocation(playerId, location, TYPE_DEATH);
    }
    
    @Override
    public Location getDeathLocation(UUID playerId) {
        return getPlayerLocation(playerId, TYPE_DEATH);
    }
    
    @Override
    public Map<UUID, Location> getAllDeathLocations() {
        return getAllPlayerLocations(TYPE_DEATH);
    }
    
    // Teleport locations
    @Override
    public void saveTeleportLocation(UUID playerId, Location location) {
        savePlayerLocation(playerId, location, TYPE_TELEPORT);
    }
    
    @Override
    public Location getTeleportLocation(UUID playerId) {
        return getPlayerLocation(playerId, TYPE_TELEPORT);
    }
    
    @Override
    public Map<UUID, Location> getAllTeleportLocations() {
        return getAllPlayerLocations(TYPE_TELEPORT);
    }
    
    // Last locations
    @Override
    public void saveLastLocation(UUID playerId, Location location) {
        savePlayerLocation(playerId, location, TYPE_LAST);
    }
    
    @Override
    public Location getLastLocation(UUID playerId) {
        return getPlayerLocation(playerId, TYPE_LAST);
    }
    
    @Override
    public Map<UUID, Location> getAllLastLocations() {
        return getAllPlayerLocations(TYPE_LAST);
    }
    
    // Home locations
    @Override
    public void saveHomeLocation(UUID playerId, Location location) {
        savePlayerLocation(playerId, location, TYPE_HOME);
    }
    
    @Override
    public Location getHomeLocation(UUID playerId) {
        return getPlayerLocation(playerId, TYPE_HOME);
    }
    
    @Override
    public boolean hasHomeLocation(UUID playerId) {
        return getPlayerLocation(playerId, TYPE_HOME) != null;
    }
    
    @Override
    public void deleteHomeLocation(UUID playerId) {
        deletePlayerLocation(playerId, TYPE_HOME);
    }
    
    @Override
    public Map<UUID, Location> getAllHomeLocations() {
        return getAllPlayerLocations(TYPE_HOME);
    }
    
    // Warp locations
    @Override
    public void saveWarpLocation(String warpName, Location location) {
        String sql = "INSERT INTO " + TABLE_WARP_LOCATIONS + 
                    " (warp_name, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE world_name=?, x=?, y=?, z=?, yaw=?, pitch=?, updated_at=CURRENT_TIMESTAMP";
        
        if (config.getType() == DatabaseConfig.DatabaseType.SQLITE) {
            sql = "INSERT OR REPLACE INTO " + TABLE_WARP_LOCATIONS + 
                  " (warp_name, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, warpName);
            stmt.setString(2, location.getWorld().getName());
            stmt.setDouble(3, location.getX());
            stmt.setDouble(4, location.getY());
            stmt.setDouble(5, location.getZ());
            stmt.setFloat(6, location.getYaw());
            stmt.setFloat(7, location.getPitch());
            
            if (config.getType() != DatabaseConfig.DatabaseType.SQLITE) {
                // For ON DUPLICATE KEY UPDATE
                stmt.setString(8, location.getWorld().getName());
                stmt.setDouble(9, location.getX());
                stmt.setDouble(10, location.getY());
                stmt.setDouble(11, location.getZ());
                stmt.setFloat(12, location.getYaw());
                stmt.setFloat(13, location.getPitch());
            }
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.severe("Failed to save warp location '" + warpName + "': " + e.getMessage());
        }
    }
    
    @Override
    public Location getWarpLocation(String warpName) {
        String sql = "SELECT world_name, x, y, z, yaw, pitch FROM " + TABLE_WARP_LOCATIONS + 
                    " WHERE warp_name = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, warpName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createLocationFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to get warp location '" + warpName + "': " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public boolean hasWarpLocation(String warpName) {
        return getWarpLocation(warpName) != null;
    }
    
    @Override
    public void deleteWarpLocation(String warpName) {
        String sql = "DELETE FROM " + TABLE_WARP_LOCATIONS + " WHERE warp_name = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, warpName);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.severe("Failed to delete warp location '" + warpName + "': " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Location> getAllWarpLocations() {
        Map<String, Location> warps = new HashMap<>();
        String sql = "SELECT warp_name, world_name, x, y, z, yaw, pitch FROM " + TABLE_WARP_LOCATIONS;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String warpName = rs.getString("warp_name");
                Location location = createLocationFromResultSet(rs);
                if (location != null) {
                    warps.put(warpName, location);
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to get all warp locations: " + e.getMessage());
        }
        
        return warps;
    }
    
    // World spawn
    @Override
    public void saveWorldSpawn(Location location) {
        String sql = "INSERT INTO " + TABLE_WORLD_SPAWN + 
                    " (world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE x=?, y=?, z=?, yaw=?, pitch=?, updated_at=CURRENT_TIMESTAMP";
        
        if (config.getType() == DatabaseConfig.DatabaseType.SQLITE) {
            sql = "INSERT OR REPLACE INTO " + TABLE_WORLD_SPAWN + 
                  " (world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String worldName = location.getWorld().getName();
            stmt.setString(1, worldName);
            stmt.setDouble(2, location.getX());
            stmt.setDouble(3, location.getY());
            stmt.setDouble(4, location.getZ());
            stmt.setFloat(5, location.getYaw());
            stmt.setFloat(6, location.getPitch());
            
            if (config.getType() != DatabaseConfig.DatabaseType.SQLITE) {
                // For ON DUPLICATE KEY UPDATE
                stmt.setDouble(7, location.getX());
                stmt.setDouble(8, location.getY());
                stmt.setDouble(9, location.getZ());
                stmt.setFloat(10, location.getYaw());
                stmt.setFloat(11, location.getPitch());
            }
            
            stmt.executeUpdate();
            
            // Set the world spawn
            if (location.getWorld() != null) {
                location.getWorld().setSpawnLocation(location);
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to save world spawn location: " + e.getMessage());
        }
    }
    
    @Override
    public Location getWorldSpawn() {
        // For now, return the spawn of the first world with a spawn location
        // In a more complex setup, you might want to handle multiple worlds
        String sql = "SELECT world_name, x, y, z, yaw, pitch FROM " + TABLE_WORLD_SPAWN + " LIMIT 1";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return createLocationFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to get world spawn location: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public void loadAll() {
        // Database storage doesn't need explicit loading as data is fetched on demand
        logger.info("Database storage initialized - data will be loaded on demand");
    }
    
    @Override
    public void saveAll() {
        // Database storage saves immediately, so this is a no-op
        logger.info("Database storage - all changes are already persisted");
    }
    
    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
    
    /**
     * Saves a player location to the database.
     */
    private void savePlayerLocation(UUID playerId, Location location, String locationType) {
        String sql = "INSERT INTO " + TABLE_PLAYER_LOCATIONS + 
                    " (player_uuid, location_type, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE world_name=?, x=?, y=?, z=?, yaw=?, pitch=?, updated_at=CURRENT_TIMESTAMP";
        
        if (config.getType() == DatabaseConfig.DatabaseType.SQLITE) {
            sql = "INSERT OR REPLACE INTO " + TABLE_PLAYER_LOCATIONS + 
                  " (player_uuid, location_type, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerId.toString());
            stmt.setString(2, locationType);
            stmt.setString(3, location.getWorld().getName());
            stmt.setDouble(4, location.getX());
            stmt.setDouble(5, location.getY());
            stmt.setDouble(6, location.getZ());
            stmt.setFloat(7, location.getYaw());
            stmt.setFloat(8, location.getPitch());
            
            if (config.getType() != DatabaseConfig.DatabaseType.SQLITE) {
                // For ON DUPLICATE KEY UPDATE
                stmt.setString(9, location.getWorld().getName());
                stmt.setDouble(10, location.getX());
                stmt.setDouble(11, location.getY());
                stmt.setDouble(12, location.getZ());
                stmt.setFloat(13, location.getYaw());
                stmt.setFloat(14, location.getPitch());
            }
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.severe("Failed to save " + locationType + " location for player " + playerId + ": " + e.getMessage());
        }
    }
    
    /**
     * Gets a player location from the database.
     */
    private Location getPlayerLocation(UUID playerId, String locationType) {
        String sql = "SELECT world_name, x, y, z, yaw, pitch FROM " + TABLE_PLAYER_LOCATIONS + 
                    " WHERE player_uuid = ? AND location_type = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerId.toString());
            stmt.setString(2, locationType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createLocationFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to get " + locationType + " location for player " + playerId + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Gets all player locations of a specific type from the database.
     */
    private Map<UUID, Location> getAllPlayerLocations(String locationType) {
        Map<UUID, Location> locations = new HashMap<>();
        String sql = "SELECT player_uuid, world_name, x, y, z, yaw, pitch FROM " + TABLE_PLAYER_LOCATIONS + 
                    " WHERE location_type = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        UUID playerId = UUID.fromString(rs.getString("player_uuid"));
                        Location location = createLocationFromResultSet(rs);
                        if (location != null) {
                            locations.put(playerId, location);
                        }
                    } catch (IllegalArgumentException e) {
                        // Skip invalid UUIDs
                        logger.warning("Invalid UUID found in database: " + rs.getString("player_uuid"));
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to get all " + locationType + " locations: " + e.getMessage());
        }
        
        return locations;
    }
    
    /**
     * Deletes a player location from the database.
     */
    private void deletePlayerLocation(UUID playerId, String locationType) {
        String sql = "DELETE FROM " + TABLE_PLAYER_LOCATIONS + " WHERE player_uuid = ? AND location_type = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerId.toString());
            stmt.setString(2, locationType);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.severe("Failed to delete " + locationType + " location for player " + playerId + ": " + e.getMessage());
        }
    }
    
    /**
     * Creates a Location object from a ResultSet.
     */
    private Location createLocationFromResultSet(ResultSet rs) throws SQLException {
        String worldName = rs.getString("world_name");
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            logger.warning("World '" + worldName + "' not found, location cannot be loaded");
            return null;
        }
        
        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        double z = rs.getDouble("z");
        float yaw = rs.getFloat("yaw");
        float pitch = rs.getFloat("pitch");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    /**
     * Migrates data from file storage to database storage.
     * 
     * @param fileStorage The file storage to migrate from
     */
    public void migrateFromFileStorage(FileLocationStorage fileStorage) {
        logger.info("Starting migration from file storage to database...");
        
        try {
            // Migrate PlayerData first (includes keepXp settings)
            Map<UUID, PlayerData> allPlayerData = fileStorage.getAllPlayerData();
            logger.info("Migrating " + allPlayerData.size() + " player data entries...");
            for (Map.Entry<UUID, PlayerData> entry : allPlayerData.entrySet()) {
                savePlayerData(entry.getKey(), entry.getValue());
            }
            
            // Migrate legacy individual location data (for players not in PlayerData format)
            // This ensures backwards compatibility with old file formats
            
            // Migrate death locations
            for (Map.Entry<UUID, Location> entry : fileStorage.getAllDeathLocations().entrySet()) {
                // Only migrate if not already in PlayerData
                if (!allPlayerData.containsKey(entry.getKey())) {
                    saveDeathLocation(entry.getKey(), entry.getValue());
                }
            }
            
            // Migrate teleport locations
            for (Map.Entry<UUID, Location> entry : fileStorage.getAllTeleportLocations().entrySet()) {
                if (!allPlayerData.containsKey(entry.getKey())) {
                    saveTeleportLocation(entry.getKey(), entry.getValue());
                }
            }
            
            // Migrate last locations
            for (Map.Entry<UUID, Location> entry : fileStorage.getAllLastLocations().entrySet()) {
                if (!allPlayerData.containsKey(entry.getKey())) {
                    saveLastLocation(entry.getKey(), entry.getValue());
                }
            }
            
            // Migrate home locations
            for (Map.Entry<UUID, Location> entry : fileStorage.getAllHomeLocations().entrySet()) {
                if (!allPlayerData.containsKey(entry.getKey())) {
                    saveHomeLocation(entry.getKey(), entry.getValue());
                }
            }
            
            // Migrate warp locations
            for (Map.Entry<String, Location> entry : fileStorage.getAllWarpLocations().entrySet()) {
                saveWarpLocation(entry.getKey(), entry.getValue());
            }
            
            // Migrate world spawn
            Location worldSpawn = fileStorage.getWorldSpawn();
            if (worldSpawn != null) {
                saveWorldSpawn(worldSpawn);
            }
            
            logger.info("Migration from file storage to database completed successfully!");
            
        } catch (Exception e) {
            logger.severe("Failed to migrate from file storage to database: " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Exception during file-to-database migration", e);
            throw new RuntimeException("Migration failed", e);
        }
    }

    /**
     * Checks if a player has a last death location stored.
     *
     * @param uniqueId The UUID of the player
     * @return True if a last death location exists, false otherwise
     */
    @Override
    public boolean hasLastDeathLocation(UUID uniqueId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_PLAYER_LOCATIONS + " WHERE player_uuid = ? AND location_type = 'death'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uniqueId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.severe("Failed to check last death location for player " + uniqueId + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a player has a last teleport location stored.
     *
     * @param uniqueId The UUID of the player
     * @return True if a last teleport location exists, false otherwise
     */
    @Override
    public boolean hasLastTeleportLocation(UUID uniqueId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_PLAYER_LOCATIONS + " WHERE player_uuid = ? AND location_type = 'teleport'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uniqueId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.severe("Failed to check last teleport location for player " + uniqueId + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a player's last teleport location.
     *
     * @param uniqueId The UUID of the player
     * @return The player's last teleport location, or null if none exists
     */
    @Override
    public Location getLastTeleportLocation(UUID uniqueId) {
        return getPlayerLocation(uniqueId, TYPE_TELEPORT);
    }

    /**
     * Retrieves complete player data including all locations and settings.
     *
     * @param playerId The UUID of the player
     * @return PlayerData object containing all player-specific data, or null if no data exists
     */
    @Override
    public PlayerData getPlayerData(UUID playerId) {
        PlayerData data = new PlayerData();
        boolean hasData = false;
        
        // Get all location types for this player
        String sql = "SELECT location_type, world, x, y, z, yaw, pitch FROM " + TABLE_PLAYER_LOCATIONS + " WHERE player_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String locationType = rs.getString("location_type");
                Location location = createLocationFromResultSet(rs);
                
                if (location != null) {
                    hasData = true;
                    switch (locationType) {
                        case TYPE_DEATH:
                            data.setLastDeath(location);
                            break;
                        case TYPE_TELEPORT:
                            data.setLastTeleport(location);
                            break;
                        case TYPE_LAST:
                            data.setLastLocation(location);
                            break;
                        case TYPE_HOME:
                            data.setHome(location);
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to retrieve player data for " + playerId + ": " + e.getMessage());
        }
        
        // Get keepXp setting - we'll store this as a player setting
        sql = "SELECT setting_value FROM pitbully_player_settings WHERE player_uuid = ? AND setting_name = 'keepXp'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String value = rs.getString("setting_value");
                data.setKeepXp("true".equalsIgnoreCase(value));
                hasData = true;
            } else {
                // Default to true for new players
                data.setKeepXp(true);
            }
        } catch (SQLException e) {
            // Table might not exist yet, that's okay - use default
            logger.info("Player settings table not found, using default keepXp value: " + e.getMessage());
            data.setKeepXp(true);
        }
        
        return hasData ? data : null;
    }

    /**
     * Saves complete player data including all locations and settings.
     *
     * @param playerId The UUID of the player
     * @param playerData The PlayerData object containing all data to save
     */
    @Override
    public void savePlayerData(UUID playerId, PlayerData playerData) {
        if (playerData == null) return;
        
        // Save locations
        if (playerData.getLastDeath() != null) {
            saveDeathLocation(playerId, playerData.getLastDeath());
        }
        if (playerData.getLastTeleport() != null) {
            saveTeleportLocation(playerId, playerData.getLastTeleport());
        }
        if (playerData.getLastLocation() != null) {
            saveLastLocation(playerId, playerData.getLastLocation());
        }
        if (playerData.getHome() != null) {
            saveHomeLocation(playerId, playerData.getHome());
        }
        
        // Save keepXp setting
        savePlayerSetting(playerId, "keepXp", String.valueOf(playerData.isKeepXp()));
    }

    /**
     * Helper method to save player-specific settings.
     */
    private void savePlayerSetting(UUID playerId, String settingName, String value) {
        // Create table if not exists
        String createTableSql = "CREATE TABLE IF NOT EXISTS pitbully_player_settings (" +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "setting_name VARCHAR(50) NOT NULL, " +
                "setting_value TEXT, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (player_uuid, setting_name)" +
                ")";
        
        String upsertSql;
        if (config.getType() == DatabaseConfig.DatabaseType.MYSQL || config.getType() == DatabaseConfig.DatabaseType.MARIADB) {
            upsertSql = "INSERT INTO pitbully_player_settings (player_uuid, setting_name, setting_value) " +
                       "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), updated_at = CURRENT_TIMESTAMP";
        } else if (config.getType() == DatabaseConfig.DatabaseType.POSTGRESQL) {
            upsertSql = "INSERT INTO pitbully_player_settings (player_uuid, setting_name, setting_value) " +
                       "VALUES (?, ?, ?) ON CONFLICT (player_uuid, setting_name) " +
                       "DO UPDATE SET setting_value = EXCLUDED.setting_value, updated_at = CURRENT_TIMESTAMP";
        } else { // SQLite
            upsertSql = "INSERT OR REPLACE INTO pitbully_player_settings (player_uuid, setting_name, setting_value) " +
                       "VALUES (?, ?, ?)";
        }
        
        try (Connection conn = dataSource.getConnection()) {
            // Create table
            try (PreparedStatement createStmt = conn.prepareStatement(createTableSql)) {
                createStmt.executeUpdate();
            }
            
            // Upsert setting
            try (PreparedStatement stmt = conn.prepareStatement(upsertSql)) {
                stmt.setString(1, playerId.toString());
                stmt.setString(2, settingName);
                stmt.setString(3, value);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.severe("Failed to save player setting " + settingName + " for " + playerId + ": " + e.getMessage());
        }
    }

    
}
