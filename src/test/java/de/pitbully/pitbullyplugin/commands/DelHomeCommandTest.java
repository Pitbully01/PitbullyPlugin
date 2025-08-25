package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.storage.LocationManager;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class DelHomeCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new DelHomeCommand().onCommand(sender, mock(Command.class), "delhome", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new DelHomeCommand().onCommand(p, mock(Command.class), "delhome", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void noHomeSet() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID(); when(p.getUniqueId()).thenReturn(id);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class)) {
            lm.when(() -> LocationManager.checkHomeLocation(id)).thenReturn(false);
            boolean handled = new DelHomeCommand().onCommand(p, mock(Command.class), "delhome", new String[]{});
            assertThat(handled).isTrue();
            verify(p).sendMessage(contains("Kein Home gesetzt"));
        }
    }

    @Test
    void deletesHomeAndSavesConfig() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID(); when(p.getUniqueId()).thenReturn(id);

    try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
         MockedStatic<PitbullyPlugin> pl = mockStatic(PitbullyPlugin.class)) {
            lm.when(() -> LocationManager.checkHomeLocation(id)).thenReturn(true);
        PitbullyPlugin plugin = mock(PitbullyPlugin.class);
        pl.when(PitbullyPlugin::getInstance).thenReturn(plugin);

            boolean handled = new DelHomeCommand().onCommand(p, mock(Command.class), "delhome", new String[]{});
            assertThat(handled).isTrue();
        lm.verify(() -> LocationManager.deleteHomeLocation(id));
            verify(plugin).saveConfig();
            verify(p).sendMessage(contains("erfolgreich gelöscht"));
        }
    }
}
