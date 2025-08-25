package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import de.pitbully.pitbullyplugin.storage.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class DelWarpCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new DelWarpCommand().onCommand(sender, mock(Command.class), "delwarp", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void invalidArgs() {
        Player p = mock(Player.class);
        boolean handled = new DelWarpCommand().onCommand(p, mock(Command.class), "delwarp", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("Warp-Namen"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new DelWarpCommand().onCommand(p, mock(Command.class), "delwarp", new String[]{"spawn"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void deletesWarpAndSavesConfig() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
    try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
         MockedStatic<PitbullyPlugin> pl = mockStatic(PitbullyPlugin.class)) {
            lm.when(() -> LocationManager.checkWarpLocation("spawn")).thenReturn(true);
        PitbullyPlugin plugin = mock(PitbullyPlugin.class);
        pl.when(PitbullyPlugin::getInstance).thenReturn(plugin);
            boolean handled = new DelWarpCommand().onCommand(p, mock(Command.class), "delwarp", new String[]{"spawn"});
            assertThat(handled).isTrue();
        lm.verify(() -> LocationManager.deleteWarpLocation("spawn"));
            verify(plugin).saveConfig();
            verify(p).sendMessage(contains("erfolgreich gelöscht"));
        }
    }
}
