package de.pitbully.pitbullyplugin.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
}
