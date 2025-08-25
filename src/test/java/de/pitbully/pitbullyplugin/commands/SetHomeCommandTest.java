package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.storage.LocationManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class SetHomeCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new SetHomeCommand().onCommand(sender, mock(Command.class), "sethome", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new SetHomeCommand().onCommand(p, mock(Command.class), "sethome", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void setsHomeAndSavesConfig() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        when(p.getLocation()).thenReturn(new Location(w, 1,64,1));

        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
             MockedStatic<PitbullyPlugin> pl = mockStatic(PitbullyPlugin.class)) {
            PitbullyPlugin plugin = mock(PitbullyPlugin.class);
            pl.when(PitbullyPlugin::getInstance).thenReturn(plugin);

            // Prepare UUID and expected location for static verification
            java.util.UUID id = java.util.UUID.randomUUID();
            when(p.getUniqueId()).thenReturn(id);
            Location expected = p.getLocation();

            boolean handled = new SetHomeCommand().onCommand(p, mock(Command.class), "sethome", new String[]{});
            assertThat(handled).isTrue();

            // Verify static call
            lm.verify(() -> LocationManager.updateHomeLocation(id, expected));
            // Verify config save and message
            verify(plugin).saveConfig();
            verify(p).sendMessage(contains("Home erfolgreich gesetzt"));
        }
    }
}
