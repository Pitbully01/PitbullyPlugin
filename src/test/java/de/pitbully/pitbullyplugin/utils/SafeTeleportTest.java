package de.pitbully.pitbullyplugin.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class SafeTeleportTest {

    @Test
    void teleport_directWhenNoConfigManager() {
        Player player = mock(Player.class);
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        Location loc = new Location(world, 0, 64, 0);
        when(player.teleport(loc)).thenReturn(true);

        try (MockedStatic<PitbullyPlugin> pluginStatic = mockStatic(PitbullyPlugin.class)) {
            pluginStatic.when(PitbullyPlugin::getInstance).thenReturn(null);

            boolean result = SafeTeleport.teleport(player, loc);
            assertThat(result).isTrue();
            verify(player).teleport(loc);
        }
    }

    @Test
    void teleport_directWhenSafetyChecksDisabled() {
        Player player = mock(Player.class);
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        Location loc = new Location(world, 5, 70, 5);
        when(player.teleport(loc)).thenReturn(true);

        ConfigManager cfg = mock(ConfigManager.class);
        when(cfg.isSafetyCheckEnabled()).thenReturn(false);

        try (MockedStatic<PitbullyPlugin> pluginStatic = mockStatic(PitbullyPlugin.class)) {
            PitbullyPlugin plugin = mock(PitbullyPlugin.class);
            when(plugin.getConfigManager()).thenReturn(cfg);
            pluginStatic.when(PitbullyPlugin::getInstance).thenReturn(plugin);

            boolean result = SafeTeleport.teleport(player, loc);
            assertThat(result).isTrue();
            verify(player).teleport(loc);
        }
    }

    @Test
    @Disabled("SafeTeleport.isSafeLocation() uses Material.isSolid() which requires full Bukkit environment")
    void teleport_findsAlternateSafeLocationIntegrationTest() {
        // This test requires integration testing with MockBukkit or Paper test environment
        // Current implementation is too tightly coupled to Bukkit API for unit testing
        // 
        // Test would verify:
        // 1. Algorithm finds safe location when original is unsafe
        // 2. Searches upward and downward within maxDistance
        // 3. Respects world height limits (-64 to 319)
        // 4. Correctly identifies safe vs unsafe locations
    }
    
    @Test
    @Disabled("Exception handling in findSafeLocation() requires full Bukkit environment")
    void teleport_handlesWorldAccessExceptions() {
        // This test would verify graceful handling of world access failures
        // Currently not unit-testable due to tight Bukkit coupling
        //
        // Test would verify:
        // 1. Returns false when block access fails
        // 2. Doesn't attempt teleportation on world errors
        // 3. Logs appropriate debug messages via ConfigManager
    }

    @Test
    void teleport_handlesNullInputs() {
        assertThat(SafeTeleport.teleport(null, mock(Location.class))).isFalse();
        assertThat(SafeTeleport.teleport(mock(Player.class), null)).isFalse();
        
        Location locationWithoutWorld = new Location(null, 0, 0, 0);
        assertThat(SafeTeleport.teleport(mock(Player.class), locationWithoutWorld)).isFalse();
    }
}
