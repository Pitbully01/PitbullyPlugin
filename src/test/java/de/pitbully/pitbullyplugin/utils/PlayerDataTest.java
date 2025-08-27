package de.pitbully.pitbullyplugin.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PlayerDataTest {

    private World world;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        world = Mockito.mock(World.class);
        Mockito.when(world.getName()).thenReturn("world");
        testLocation = new Location(world, 10, 64, -20, 90f, 0f);
    }

    @Test
    void testEmptyPlayerData() {
        PlayerData data = new PlayerData();
        
        assertThat(data.getLastDeath()).isNull();
        assertThat(data.getLastTeleport()).isNull();
        assertThat(data.getLastLocation()).isNull();
        assertThat(data.getHome()).isNull();
        assertThat(data.isKeepXp()).isTrue(); // Default value is now true
        assertThat(data.isEmpty()).isTrue();
    }

    @Test
    void testPlayerDataWithLocations() {
        PlayerData data = new PlayerData();
        
        data.setLastDeath(testLocation);
        data.setLastTeleport(testLocation);
        data.setLastLocation(testLocation);
        data.setHome(testLocation);
        data.setKeepXp(true);
        
        assertThat(data.getLastDeath()).isEqualTo(testLocation);
        assertThat(data.getLastTeleport()).isEqualTo(testLocation);
        assertThat(data.getLastLocation()).isEqualTo(testLocation);
        assertThat(data.getHome()).isEqualTo(testLocation);
        assertThat(data.isKeepXp()).isTrue();
        assertThat(data.isEmpty()).isFalse();
    }

    @Test
    void testKeepXpToggle() {
        PlayerData data = new PlayerData();
        
        assertThat(data.isKeepXp()).isTrue(); // Default is now true
        
        data.toggleSetKeepXp();
        assertThat(data.isKeepXp()).isFalse();
        
        data.toggleSetKeepXp();
        assertThat(data.isKeepXp()).isTrue();
    }

    @Test
    void testKeepXpSetter() {
        PlayerData data = new PlayerData();
        
        data.setKeepXp(true);
        assertThat(data.isKeepXp()).isTrue();
        
        data.setKeepXp(false);
        assertThat(data.isKeepXp()).isFalse();
    }

    @Test
    void testToConfigSerialization() {
        PlayerData data = new PlayerData();
        data.setLastDeath(testLocation);
        data.setLastTeleport(testLocation);
        data.setLastLocation(testLocation);
        data.setHome(testLocation);
        data.setKeepXp(true);
        
        ConfigurationSection section = new MemoryConfiguration();
        data.toConfig(section);
        
        assertThat(section.get("lastDeath")).isEqualTo(testLocation);
        assertThat(section.get("lastTeleport")).isEqualTo(testLocation);
        assertThat(section.get("lastLocation")).isEqualTo(testLocation);
        assertThat(section.get("home")).isEqualTo(testLocation);
        assertThat(section.get("keepXp")).isEqualTo(true);
    }

    @Test
    void testFromConfigDeserialization() {
        ConfigurationSection section = new MemoryConfiguration();
        section.set("lastDeath", testLocation);
        section.set("lastTeleport", testLocation);
        section.set("lastLocation", testLocation);
        section.set("home", testLocation);
        section.set("keepXp", true);
        
        PlayerData data = PlayerData.fromConfig(section);
        
        assertThat(data.getLastDeath()).isEqualTo(testLocation);
        assertThat(data.getLastTeleport()).isEqualTo(testLocation);
        assertThat(data.getLastLocation()).isEqualTo(testLocation);
        assertThat(data.getHome()).isEqualTo(testLocation);
        assertThat(data.isKeepXp()).isTrue();
    }

    @Test
    void testFromConfigWithMissingKeepXp() {
        ConfigurationSection section = new MemoryConfiguration();
        section.set("home", testLocation);
        // keepXp is missing - should default to true
        
        PlayerData data = PlayerData.fromConfig(section);
        
        assertThat(data.getHome()).isEqualTo(testLocation);
        assertThat(data.isKeepXp()).isTrue(); // Default value
    }

    @Test
    void testFromConfigWithNullSection() {
        PlayerData data = PlayerData.fromConfig(null);
        
        assertThat(data).isNotNull();
        assertThat(data.isEmpty()).isTrue();
        assertThat(data.isKeepXp()).isTrue(); // Default constructor value is now true
    }

    @Test
    void testIsEmptyWithOnlyKeepXp() {
        PlayerData data = new PlayerData();
        data.setKeepXp(false); // Set to non-default value
        
        // Should not be empty if keepXp is set to false (different from default true)
        assertThat(data.isEmpty()).isFalse();
        
        // Test with default value
        data.setKeepXp(true); // Set to default value
        assertThat(data.isEmpty()).isTrue(); // Should be empty when all at defaults
    }

    @Test
    void testToConfigWithNullSection() {
        PlayerData data = new PlayerData();
        data.setHome(testLocation);
        
        // Should not throw exception
        data.toConfig(null);
        // No assertion needed - just ensuring no exception is thrown
    }

    @Test
    void testRoundTripSerialization() {
        PlayerData original = new PlayerData();
        original.setLastDeath(testLocation);
        original.setKeepXp(true);
        
        ConfigurationSection section = new MemoryConfiguration();
        original.toConfig(section);
        
        PlayerData restored = PlayerData.fromConfig(section);
        
        assertThat(restored.getLastDeath()).isEqualTo(original.getLastDeath());
        assertThat(restored.isKeepXp()).isEqualTo(original.isKeepXp());
        assertThat(restored.isEmpty()).isEqualTo(original.isEmpty());
    }
}
