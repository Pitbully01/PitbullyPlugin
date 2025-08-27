package de.pitbully.pitbullyplugin.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import de.pitbully.pitbullyplugin.utils.PlayerData;

class FileLocationStorageTest {

    @Test
    void saveAll_writesPlayersSection_andClearsLegacySections() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-file-storage-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            UUID id = UUID.randomUUID();
            // World may be null for the purpose of this serialization structure test
            Location loc = new Location(null, 1, 64, 1);

            storage.saveDeathLocation(id, loc);
            storage.saveTeleportLocation(id, loc);
            storage.saveLastLocation(id, loc);
            storage.saveHomeLocation(id, loc);

            storage.saveAll();

            File locationsFile = new File(tempDir, "locations.yml");
            assertThat(locationsFile).exists();

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(locationsFile);
            String base = "players." + id;

            assertThat(cfg.contains(base + ".lastDeath")).isTrue();
            assertThat(cfg.contains(base + ".lastTeleport")).isTrue();
            assertThat(cfg.contains(base + ".lastLocation")).isTrue();
            assertThat(cfg.contains(base + ".home")).isTrue();

            // Legacy sections should be cleared
            assertThat(cfg.getConfigurationSection("lastDeathLocations")).isNull();
            assertThat(cfg.getConfigurationSection("lastTeleportLocations")).isNull();
            assertThat(cfg.getConfigurationSection("lastLocations")).isNull();
            assertThat(cfg.getConfigurationSection("homeLocations")).isNull();
        } finally {
            // Best-effort cleanup
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void loadAll_migratesLegacySections_intoPlayers_andSaveClearsLegacy() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-file-storage-migration").toFile();
        try {
            // Seed a legacy-style locations.yml
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            UUID id = UUID.randomUUID();
            String uuid = id.toString();
            Location loc = new Location(null, 10, 70, -3);
            legacy.set("lastDeathLocations." + uuid, loc);
            legacy.set("lastTeleportLocations." + uuid, loc);
            legacy.set("lastLocations." + uuid, loc);
            legacy.set("homeLocations." + uuid, loc);
            legacy.save(locations);

            // Now initialize storage which should load and migrate
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            // After load, legacy should be reflected in getters
            assertThat(storage.getDeathLocation(id)).isNotNull();
            assertThat(storage.getTeleportLocation(id)).isNotNull();
            assertThat(storage.getLastLocation(id)).isNotNull();
            assertThat(storage.getHomeLocation(id)).isNotNull();

            storage.saveAll();

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(locations);
            String base = "players." + uuid;
            assertThat(cfg.contains(base + ".lastDeath")).isTrue();
            assertThat(cfg.contains(base + ".lastTeleport")).isTrue();
            assertThat(cfg.contains(base + ".lastLocation")).isTrue();
            assertThat(cfg.contains(base + ".home")).isTrue();

            assertThat(cfg.getConfigurationSection("lastDeathLocations")).isNull();
            assertThat(cfg.getConfigurationSection("lastTeleportLocations")).isNull();
            assertThat(cfg.getConfigurationSection("lastLocations")).isNull();
            assertThat(cfg.getConfigurationSection("homeLocations")).isNull();
        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testPlayerDataOperations() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-playerdata-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            UUID playerId = UUID.randomUUID();
            Location testLocation = new Location(null, 100, 64, -50);

            // Create PlayerData
            PlayerData originalData = new PlayerData();
            originalData.setLastDeath(testLocation);
            originalData.setLastTeleport(testLocation);
            originalData.setLastLocation(testLocation);
            originalData.setHome(testLocation);
            originalData.setKeepXp(true);

            // Save PlayerData
            storage.savePlayerData(playerId, originalData);

            // Retrieve PlayerData
            PlayerData retrievedData = storage.getPlayerData(playerId);

            assertThat(retrievedData).isNotNull();
            assertThat(retrievedData.getLastDeath()).isEqualTo(testLocation);
            assertThat(retrievedData.getLastTeleport()).isEqualTo(testLocation);
            assertThat(retrievedData.getLastLocation()).isEqualTo(testLocation);
            assertThat(retrievedData.getHome()).isEqualTo(testLocation);
            assertThat(retrievedData.isKeepXp()).isTrue();

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testPlayerDataWithNullData() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-playerdata-null-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            UUID playerId = UUID.randomUUID();

            // Test saving null PlayerData (should handle gracefully)
            storage.savePlayerData(playerId, null);

            // Test getting non-existent PlayerData
            PlayerData retrievedData = storage.getPlayerData(playerId);
            assertThat(retrievedData).isNull();

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testPlayerDataPersistence() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-playerdata-persistence-test").toFile();
        try {
            Logger logger = Logger.getLogger("Test");
            UUID playerId = UUID.randomUUID();
            Location testLocation = new Location(null, 25, 80, 15);

            // Create and save data with first storage instance
            {
                FileLocationStorage storage = new FileLocationStorage(tempDir, logger);
                PlayerData data = new PlayerData();
                data.setHome(testLocation);
                data.setKeepXp(false); // Set to non-default value
                storage.savePlayerData(playerId, data);
                storage.saveAll();
            }

            // Load data with new storage instance
            {
                FileLocationStorage storage = new FileLocationStorage(tempDir, logger);
                PlayerData retrievedData = storage.getPlayerData(playerId);

                assertThat(retrievedData).isNotNull();
                assertThat(retrievedData.getHome()).isEqualTo(testLocation);
                assertThat(retrievedData.isKeepXp()).isFalse(); // Should persist non-default value
            }

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }

    @Test
    void testMigrationWithKeepXpDefaults() throws Exception {
        File tempDir = Files.createTempDirectory("pitbully-migration-keepxp-test").toFile();
        try {
            // Create legacy format without keepXp
            File locations = new File(tempDir, "locations.yml");
            YamlConfiguration legacy = new YamlConfiguration();
            UUID id = UUID.randomUUID();
            String uuid = id.toString();
            Location loc = new Location(null, 5, 65, 10);
            legacy.set("homeLocations." + uuid, loc);
            legacy.save(locations);

            // Initialize storage and load
            Logger logger = Logger.getLogger("Test");
            FileLocationStorage storage = new FileLocationStorage(tempDir, logger);

            // Get PlayerData - should have migrated home and default keepXp
            PlayerData data = storage.getPlayerData(id);
            assertThat(data).isNotNull();
            assertThat(data.getHome()).isEqualTo(loc);
            assertThat(data.isKeepXp()).isTrue(); // Should default to true for migrated data

        } finally {
            try { new File(tempDir, "locations.yml").delete(); } catch (Exception ignored) {}
            try { tempDir.delete(); } catch (Exception ignored) {}
        }
    }
}
