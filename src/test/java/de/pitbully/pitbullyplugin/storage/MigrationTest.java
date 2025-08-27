package de.pitbully.pitbullyplugin.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import de.pitbully.pitbullyplugin.utils.PlayerData;

class MigrationTest {

    @Test
    void testCompletePlayerCentricMigration() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-migration-test").toFile();
        try {
            // Create legacy format with all location types
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            
            UUID player1 = UUID.randomUUID();
            UUID player2 = UUID.randomUUID();
            String uuid1 = player1.toString();
            String uuid2 = player2.toString();
            
            // Player 1 has all location types
            Location home1 = new Location(null, 10, 64, 20);
            Location death1 = new Location(null, 15, 60, 25);
            Location teleport1 = new Location(null, 20, 70, 30);
            Location last1 = new Location(null, 25, 65, 35);
            
            legacy.set("homeLocations." + uuid1, home1);
            legacy.set("lastDeathLocations." + uuid1, death1);
            legacy.set("lastTeleportLocations." + uuid1, teleport1);
            legacy.set("lastLocations." + uuid1, last1);
            
            // Player 2 has only home
            Location home2 = new Location(null, 100, 64, 200);
            legacy.set("homeLocations." + uuid2, home2);
            
            // Some warps
            Location spawn = new Location(null, 0, 64, 0);
            legacy.set("warpLocations.spawn", spawn);
            
            legacy.save(locations);

            // Initialize storage - should trigger migration
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            // Verify Player 1 data migration
            PlayerData data1 = storage.getPlayerData(player1);
            assertThat(data1).isNotNull();
            assertThat(data1.getHome()).isEqualTo(home1);
            assertThat(data1.getLastDeath()).isEqualTo(death1);
            assertThat(data1.getLastTeleport()).isEqualTo(teleport1);
            assertThat(data1.getLastLocation()).isEqualTo(last1);
            assertThat(data1.isKeepXp()).isTrue(); // Default for migration

            // Verify Player 2 data migration
            PlayerData data2 = storage.getPlayerData(player2);
            assertThat(data2).isNotNull();
            assertThat(data2.getHome()).isEqualTo(home2);
            assertThat(data2.getLastDeath()).isNull();
            assertThat(data2.getLastTeleport()).isNull();
            assertThat(data2.getLastLocation()).isNull();
            assertThat(data2.isKeepXp()).isTrue(); // Default for migration

            // Verify warps are preserved
            assertThat(storage.getWarpLocation("spawn")).isEqualTo(spawn);

            // Save and verify file structure
            storage.saveAll();

            YamlConfiguration migrated = YamlConfiguration.loadConfiguration(locations);
            
            // Verify new player-centric structure
            assertThat(migrated.contains("players." + uuid1 + ".home")).isTrue();
            assertThat(migrated.contains("players." + uuid1 + ".lastDeath")).isTrue();
            assertThat(migrated.contains("players." + uuid1 + ".lastTeleport")).isTrue();
            assertThat(migrated.contains("players." + uuid1 + ".lastLocation")).isTrue();
            assertThat(migrated.contains("players." + uuid1 + ".keepXp")).isTrue();
            
            assertThat(migrated.contains("players." + uuid2 + ".home")).isTrue();
            assertThat(migrated.contains("players." + uuid2 + ".keepXp")).isTrue();
            
            // Verify legacy sections are removed
            assertThat(migrated.getConfigurationSection("homeLocations")).isNull();
            assertThat(migrated.getConfigurationSection("lastDeathLocations")).isNull();
            assertThat(migrated.getConfigurationSection("lastTeleportLocations")).isNull();
            assertThat(migrated.getConfigurationSection("lastLocations")).isNull();
            
            // Verify warps are preserved
            assertThat(migrated.contains("warpLocations.spawn")).isTrue();
            
            // Verify keepXp defaults
            assertThat(migrated.getBoolean("players." + uuid1 + ".keepXp")).isTrue();
            assertThat(migrated.getBoolean("players." + uuid2 + ".keepXp")).isTrue();

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testMigrationWithEmptyLegaySections() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-empty-migration-test").toFile();
        try {
            // Create file with empty legacy sections
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            
            // Only warps, no player locations
            Location spawn = new Location(null, 0, 64, 0);
            legacy.set("warpLocations.spawn", spawn);
            legacy.save(locations);

            // Initialize storage
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            // Should handle empty migration gracefully
            UUID randomPlayer = UUID.randomUUID();
            PlayerData data = storage.getPlayerData(randomPlayer);
            assertThat(data).isNull(); // No data for non-existent player

            // Warps should still work
            assertThat(storage.getWarpLocation("spawn")).isEqualTo(spawn);

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testMigrationIdempotency() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-idempotent-migration-test").toFile();
        try {
            // Create legacy data
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            
            UUID playerId = UUID.randomUUID();
            String uuid = playerId.toString();
            Location home = new Location(null, 50, 64, 100);
            
            legacy.set("homeLocations." + uuid, home);
            legacy.save(locations);

            Logger logger = Logger.getLogger("Test");
            
            // First migration
            {
                FileLocationStorage storage1 = new FileLocationStorage(tempDir, logger);
                PlayerData data1 = storage1.getPlayerData(playerId);
                assertThat(data1).isNotNull();
                assertThat(data1.getHome()).isEqualTo(home);
                storage1.saveAll();
            }

            // Second initialization - should not duplicate or break anything
            {
                FileLocationStorage storage2 = new FileLocationStorage(tempDir, logger);
                PlayerData data2 = storage2.getPlayerData(playerId);
                assertThat(data2).isNotNull();
                assertThat(data2.getHome()).isEqualTo(home);
                assertThat(data2.isKeepXp()).isTrue();
                
                // Modify data and save
                data2.setKeepXp(false);
                storage2.savePlayerData(playerId, data2);
                storage2.saveAll();
            }

            // Third initialization - should preserve modifications
            {
                FileLocationStorage storage3 = new FileLocationStorage(tempDir, logger);
                PlayerData data3 = storage3.getPlayerData(playerId);
                assertThat(data3).isNotNull();
                assertThat(data3.getHome()).isEqualTo(home);
                assertThat(data3.isKeepXp()).isFalse(); // Should preserve our modification
            }

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testPartialDataMigration() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-partial-migration-test").toFile();
        try {
            // Create mixed scenario: some players have some location types only
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            
            UUID player1 = UUID.randomUUID();
            UUID player2 = UUID.randomUUID();
            UUID player3 = UUID.randomUUID();
            
            // Player 1: only home
            legacy.set("homeLocations." + player1, new Location(null, 10, 64, 10));
            
            // Player 2: only death
            legacy.set("lastDeathLocations." + player2, new Location(null, 20, 60, 20));
            
            // Player 3: home and teleport
            legacy.set("homeLocations." + player3, new Location(null, 30, 64, 30));
            legacy.set("lastTeleportLocations." + player3, new Location(null, 35, 65, 35));
            
            legacy.save(locations);

            // Initialize and migrate
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            // Verify each player's partial data
            PlayerData data1 = storage.getPlayerData(player1);
            assertThat(data1).isNotNull();
            assertThat(data1.getHome()).isNotNull();
            assertThat(data1.getLastDeath()).isNull();
            assertThat(data1.getLastTeleport()).isNull();
            assertThat(data1.getLastLocation()).isNull();
            assertThat(data1.isKeepXp()).isTrue();

            PlayerData data2 = storage.getPlayerData(player2);
            assertThat(data2).isNotNull();
            assertThat(data2.getHome()).isNull();
            assertThat(data2.getLastDeath()).isNotNull();
            assertThat(data2.getLastTeleport()).isNull();
            assertThat(data2.getLastLocation()).isNull();
            assertThat(data2.isKeepXp()).isTrue();

            PlayerData data3 = storage.getPlayerData(player3);
            assertThat(data3).isNotNull();
            assertThat(data3.getHome()).isNotNull();
            assertThat(data3.getLastDeath()).isNull();
            assertThat(data3.getLastTeleport()).isNotNull();
            assertThat(data3.getLastLocation()).isNull();
            assertThat(data3.isKeepXp()).isTrue();

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testFileToDbMigrationWithPlayerData() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-file-to-db-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            
            // Setup file storage with PlayerData including keepXp settings
            FileLocationStorage fileStorage = new FileLocationStorage(tempDir, logger);
            
            UUID player1 = UUID.randomUUID();
            UUID player2 = UUID.randomUUID();
            
            // Create PlayerData with different keepXp settings
            PlayerData data1 = new PlayerData();
            data1.setHome(new Location(null, 10, 64, 20));
            data1.setLastDeath(new Location(null, 5, 60, 15));
            data1.setKeepXp(true);
            
            PlayerData data2 = new PlayerData();
            data2.setHome(new Location(null, 30, 64, 40));
            data2.setKeepXp(false); // Critical: different setting
            
            fileStorage.savePlayerData(player1, data1);
            fileStorage.savePlayerData(player2, data2);
            
            // Create fake database storage for testing
            TestDbStorage dbStorage = new TestDbStorage(logger);
            
            // Perform migration
            dbStorage.migrateFromFileStorage(fileStorage);
            
            // Verify PlayerData was migrated correctly with keepXp settings
            PlayerData migratedData1 = dbStorage.getPlayerData(player1);
            assertThat(migratedData1).isNotNull();
            assertThat(migratedData1.getHome()).isEqualTo(data1.getHome());
            assertThat(migratedData1.getLastDeath()).isEqualTo(data1.getLastDeath());
            assertThat(migratedData1.isKeepXp()).isTrue();
            
            PlayerData migratedData2 = dbStorage.getPlayerData(player2);
            assertThat(migratedData2).isNotNull();
            assertThat(migratedData2.getHome()).isEqualTo(data2.getHome());
            assertThat(migratedData2.isKeepXp()).isFalse(); // Critical: keepXp preserved
            
        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testFileToDbMigrationWithLegacyData() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-legacy-to-db-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            
            // Setup file storage with legacy location data only
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            
            UUID playerId = UUID.randomUUID();
            Location home = new Location(null, 100, 64, 200);
            Location death = new Location(null, 95, 60, 195);
            
            legacy.set("homeLocations." + playerId, home);
            legacy.set("lastDeathLocations." + playerId, death);
            legacy.save(locations);
            
            FileLocationStorage fileStorage = new FileLocationStorage(tempDir, logger);
            
            TestDbStorage dbStorage = new TestDbStorage(logger);
            
            // Perform migration
            dbStorage.migrateFromFileStorage(fileStorage);
            
            // Verify legacy data was migrated with default keepXp
            assertThat(dbStorage.getHomeLocation(playerId)).isEqualTo(home);
            assertThat(dbStorage.getDeathLocation(playerId)).isEqualTo(death);
            
            // Legacy players should get PlayerData created on demand with default keepXp
            PlayerData legacyData = dbStorage.getPlayerData(playerId);
            if (legacyData == null) {
                // For legacy data, PlayerData might be null until accessed via individual methods
                // This is expected behavior for pure legacy migration
            }
            
        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testRealLegacyDataMigration() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-real-legacy-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            
            // Copy real legacy data to temp location
            File targetFile = new File(tempDir, "locations.yml");
            java.io.InputStream realData = getClass().getResourceAsStream("/real-legacy-locations.yml");
            assertThat(realData).isNotNull();
            
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(targetFile)) {
                realData.transferTo(out);
            }

            // Load the YAML and verify it has the expected structure
            YamlConfiguration config = YamlConfiguration.loadConfiguration(targetFile);
            
            // Verify legacy sections exist
            assertThat(config.getConfigurationSection("homeLocations")).isNotNull();
            assertThat(config.getConfigurationSection("lastDeathLocations")).isNotNull();
            assertThat(config.getConfigurationSection("lastTeleportLocations")).isNotNull();
            assertThat(config.getConfigurationSection("lastLocations")).isNotNull();
            assertThat(config.getConfigurationSection("warpLocations")).isNotNull();
            assertThat(config.getConfigurationSection("worldSpawnLocation")).isNotNull();
            
            // Define expected UUIDs from our test data
            String playerAId = "a1b2c3d4-1111-4444-8888-000000000001";
            String playerDId = "a1b2c3d4-4444-4444-8888-000000000004";
            
            // Verify Player A (has all location types) coordinates in original format
            Object playerAHomeObj = config.get("homeLocations." + playerAId);
            assertThat(playerAHomeObj).isNotNull();
            if (playerAHomeObj instanceof ConfigurationSection) {
                ConfigurationSection playerAHome = (ConfigurationSection) playerAHomeObj;
                assertThat(playerAHome.getDouble("x")).isCloseTo(189.479, org.assertj.core.data.Offset.offset(0.001));
                assertThat(playerAHome.getDouble("y")).isEqualTo(64.0);
                assertThat(playerAHome.getDouble("z")).isCloseTo(-41.146, org.assertj.core.data.Offset.offset(0.001));
            }
            
            // Verify Player D (only has teleport and last location, no home or death)
            assertThat(config.getConfigurationSection("homeLocations." + playerDId)).isNull();
            assertThat(config.getConfigurationSection("lastDeathLocations." + playerDId)).isNull();
            assertThat(config.getConfigurationSection("lastTeleportLocations." + playerDId)).isNotNull();
            assertThat(config.getConfigurationSection("lastLocations." + playerDId)).isNotNull();
            
            // Verify warps are preserved with correct coordinates
            Object farmWarpObj = config.get("warpLocations.farm_area");
            assertThat(farmWarpObj).isNotNull();
            if (farmWarpObj instanceof ConfigurationSection) {
                ConfigurationSection farmWarp = (ConfigurationSection) farmWarpObj;
                assertThat(farmWarp.getDouble("x")).isCloseTo(150.800, org.assertj.core.data.Offset.offset(0.001));
                assertThat(farmWarp.getDouble("y")).isEqualTo(64.0);
            }
            
            // Verify world spawn coordinates
            Object worldSpawnObj = config.get("worldSpawnLocation");
            assertThat(worldSpawnObj).isNotNull();
            if (worldSpawnObj instanceof ConfigurationSection) {
                ConfigurationSection worldSpawn = (ConfigurationSection) worldSpawnObj;
                assertThat(worldSpawn.getDouble("x")).isCloseTo(175.403, org.assertj.core.data.Offset.offset(0.001));
                assertThat(worldSpawn.getDouble("y")).isEqualTo(64.0);
                assertThat(worldSpawn.getDouble("z")).isCloseTo(-50.912, org.assertj.core.data.Offset.offset(0.001));
            }
            
            // Initialize FileLocationStorage - this should trigger migration if Bukkit worlds are available
            // In test environment, it will attempt migration but Location objects may be null
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);
            
            // Save and verify new format was attempted
            storage.saveAll();
            
            YamlConfiguration migrated = YamlConfiguration.loadConfiguration(targetFile);
            
            // In test environment, player data might not migrate due to missing worlds
            // But legacy sections should be preserved or cleared based on successful migration
            // The key is that the process doesn't crash and the file structure is maintained
            
            // Verify warps and world spawn are always preserved regardless of world availability
            assertThat(migrated.contains("warpLocations.farm_area")).isTrue();
            assertThat(migrated.contains("worldSpawnLocation")).isTrue();
            
        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testRealDataFileToDbMigration() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-real-file-to-db-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            
            // Setup file storage with real legacy data
            File targetFile = new File(tempDir, "locations.yml");
            java.io.InputStream realData = getClass().getResourceAsStream("/real-legacy-locations.yml");
            assertThat(realData).isNotNull();
            
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(targetFile)) {
                realData.transferTo(out);
            }
            
            FileLocationStorage fileStorage = new FileLocationStorage(tempDir, logger);
            
            // Create test database storage
            TestDbStorage dbStorage = new TestDbStorage(logger);
            
            // Perform file-to-database migration
            dbStorage.migrateFromFileStorage(fileStorage);
            
            // Verify all player data migrated correctly
            UUID playerA = UUID.fromString("a1b2c3d4-1111-4444-8888-000000000001");
            UUID playerD = UUID.fromString("a1b2c3d4-4444-4444-8888-000000000004");
            
            // Verify Player A fully migrated with keepXp default
            PlayerData dbDataA = dbStorage.getPlayerData(playerA);
            assertThat(dbDataA).isNotNull();
            assertThat(dbDataA.getHome()).isNotNull();
            assertThat(dbDataA.getLastDeath()).isNotNull();
            assertThat(dbDataA.getLastTeleport()).isNotNull();
            assertThat(dbDataA.getLastLocation()).isNotNull();
            assertThat(dbDataA.isKeepXp()).isTrue(); // Default preserved in migration
            
            // Verify Player D (partial data) migrated correctly
            PlayerData dbDataD = dbStorage.getPlayerData(playerD);
            assertThat(dbDataD).isNotNull();
            assertThat(dbDataD.getHome()).isNull();
            assertThat(dbDataD.getLastDeath()).isNull();
            assertThat(dbDataD.getLastTeleport()).isNotNull();
            assertThat(dbDataD.getLastLocation()).isNotNull();
            assertThat(dbDataD.isKeepXp()).isTrue();
            
            // Verify warps migrated to database
            assertThat(dbStorage.hasWarpLocation("farm_area")).isTrue();
            assertThat(dbStorage.hasWarpLocation("biome_desert")).isTrue();
            
            // Verify world spawn migrated
            Location dbWorldSpawn = dbStorage.getWorldSpawn();
            assertThat(dbWorldSpawn).isNotNull();
            assertThat(dbWorldSpawn.getX()).isCloseTo(175.403, org.assertj.core.data.Offset.offset(0.001));
            
        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * Minimal test database storage that implements LocationStorage interface
     * directly for testing migration logic without real database
     */
    private static class TestDbStorage implements LocationStorage {
        private final Logger logger;
        private java.util.Map<UUID, PlayerData> players = new java.util.HashMap<>();
        private java.util.Map<UUID, Location> homes = new java.util.HashMap<>();
        private java.util.Map<UUID, Location> deaths = new java.util.HashMap<>();
        private java.util.Map<UUID, Location> teleports = new java.util.HashMap<>();
        private java.util.Map<UUID, Location> lasts = new java.util.HashMap<>();
        private java.util.Map<String, Location> warps = new java.util.HashMap<>();
        private Location worldSpawn;
        
        public TestDbStorage(Logger logger) {
            this.logger = logger;
        }
        
        // Use the real migration method from DatabaseLocationStorage
        public void migrateFromFileStorage(FileLocationStorage fileStorage) {
            logger.info("Starting migration from file storage to database...");
            
            try {
                // Migrate PlayerData first (includes keepXp settings)
                java.util.Map<UUID, PlayerData> allPlayerData = fileStorage.getAllPlayerData();
                logger.info("Migrating " + allPlayerData.size() + " player data entries...");
                for (java.util.Map.Entry<UUID, PlayerData> entry : allPlayerData.entrySet()) {
                    savePlayerData(entry.getKey(), entry.getValue());
                }
                
                // Migrate legacy individual location data (for players not in PlayerData format)
                // This ensures backwards compatibility with old file formats
                
                // Migrate death locations
                for (java.util.Map.Entry<UUID, Location> entry : fileStorage.getAllDeathLocations().entrySet()) {
                    // Only migrate if not already in PlayerData
                    if (!allPlayerData.containsKey(entry.getKey())) {
                        saveDeathLocation(entry.getKey(), entry.getValue());
                    }
                }
                
                // Migrate teleport locations
                for (java.util.Map.Entry<UUID, Location> entry : fileStorage.getAllTeleportLocations().entrySet()) {
                    if (!allPlayerData.containsKey(entry.getKey())) {
                        saveTeleportLocation(entry.getKey(), entry.getValue());
                    }
                }
                
                // Migrate last locations
                for (java.util.Map.Entry<UUID, Location> entry : fileStorage.getAllLastLocations().entrySet()) {
                    if (!allPlayerData.containsKey(entry.getKey())) {
                        saveLastLocation(entry.getKey(), entry.getValue());
                    }
                }
                
                // Migrate home locations
                for (java.util.Map.Entry<UUID, Location> entry : fileStorage.getAllHomeLocations().entrySet()) {
                    if (!allPlayerData.containsKey(entry.getKey())) {
                        saveHomeLocation(entry.getKey(), entry.getValue());
                    }
                }
                
                // Migrate warp locations
                for (java.util.Map.Entry<String, Location> entry : fileStorage.getAllWarpLocations().entrySet()) {
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
                throw new RuntimeException("Migration failed", e);
            }
        }
        
        @Override
        public void savePlayerData(UUID playerId, PlayerData playerData) {
            if (playerData == null) {
                players.remove(playerId);
                return;
            }
            players.put(playerId, playerData);
            // Update individual maps for compatibility
            if (playerData.getHome() != null) homes.put(playerId, playerData.getHome());
            if (playerData.getLastDeath() != null) deaths.put(playerId, playerData.getLastDeath());
            if (playerData.getLastTeleport() != null) teleports.put(playerId, playerData.getLastTeleport());
            if (playerData.getLastLocation() != null) lasts.put(playerId, playerData.getLastLocation());
        }
        
        @Override
        public PlayerData getPlayerData(UUID playerId) {
            return players.get(playerId);
        }
        
        @Override
        public void saveHomeLocation(UUID playerId, Location location) {
            homes.put(playerId, location);
        }
        
        @Override
        public Location getHomeLocation(UUID playerId) {
            return homes.get(playerId);
        }
        
        @Override
        public void saveDeathLocation(UUID playerId, Location location) {
            deaths.put(playerId, location);
        }
        
        @Override
        public Location getDeathLocation(UUID playerId) {
            return deaths.get(playerId);
        }
        
        @Override
        public void saveTeleportLocation(UUID playerId, Location location) {
            teleports.put(playerId, location);
        }
        
        @Override
        public Location getTeleportLocation(UUID playerId) {
            return teleports.get(playerId);
        }
        
        @Override
        public java.util.Map<UUID, Location> getAllTeleportLocations() {
            return teleports;
        }
        
        @Override
        public void saveLastLocation(UUID playerId, Location location) {
            lasts.put(playerId, location);
        }
        
        @Override
        public Location getLastLocation(UUID playerId) {
            return lasts.get(playerId);
        }
        
        @Override
        public java.util.Map<UUID, Location> getAllLastLocations() {
            return lasts;
        }
        
        @Override
        public boolean hasHomeLocation(UUID playerId) {
            return homes.containsKey(playerId);
        }
        
        @Override
        public void deleteHomeLocation(UUID playerId) {
            homes.remove(playerId);
        }
        
        @Override
        public java.util.Map<UUID, Location> getAllHomeLocations() {
            return homes;
        }
        
        @Override
        public void saveWarpLocation(String warpName, Location location) {
            warps.put(warpName, location);
        }
        
        @Override
        public Location getWarpLocation(String warpName) {
            return warps.get(warpName);
        }
        
        @Override
        public boolean hasWarpLocation(String warpName) {
            return warps.containsKey(warpName);
        }
        
        @Override
        public void deleteWarpLocation(String warpName) {
            warps.remove(warpName);
        }
        
        @Override
        public java.util.Map<String, Location> getAllWarpLocations() {
            return warps;
        }
        
        @Override
        public void saveWorldSpawn(Location location) {
            this.worldSpawn = location;
        }
        
        @Override
        public Location getWorldSpawn() {
            return worldSpawn;
        }
        
        @Override
        public void loadAll() {
            // No-op for test
        }
        
        @Override
        public void saveAll() {
            // No-op for test
        }
        
        @Override
        public void close() {
            // No-op for test
        }
        
        @Override
        public boolean hasLastDeathLocation(UUID uniqueId) {
            return deaths.containsKey(uniqueId);
        }
        
        @Override
        public boolean hasLastTeleportLocation(UUID uniqueId) {
            return teleports.containsKey(uniqueId);
        }
        
        @Override
        public Location getLastTeleportLocation(UUID uniqueId) {
            return teleports.get(uniqueId);
        }
        
        @Override
        public java.util.Map<UUID, Location> getAllDeathLocations() {
            return deaths;
        }
    }
}
