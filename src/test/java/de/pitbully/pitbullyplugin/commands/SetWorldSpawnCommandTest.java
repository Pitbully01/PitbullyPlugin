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

class SetWorldSpawnCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new SetWorldSpawnCommand().onCommand(sender, mock(Command.class), "setspawn", new String[]{});
        assertThat(handled).isFalse();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new SetWorldSpawnCommand().onCommand(p, mock(Command.class), "setspawn", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void setsSpawnAndSavesConfig() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        when(p.getLocation()).thenReturn(new Location(w, 1,64,1));

    try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
         MockedStatic<PitbullyPlugin> pl = mockStatic(PitbullyPlugin.class)) {
        PitbullyPlugin plugin = mock(PitbullyPlugin.class);
        pl.when(PitbullyPlugin::getInstance).thenReturn(plugin);
        Location expected = p.getLocation();
        boolean handled = new SetWorldSpawnCommand().onCommand(p, mock(Command.class), "setspawn", new String[]{});
            assertThat(handled).isTrue();
        lm.verify(() -> LocationManager.updateWorldSpawnLocation(expected));
            verify(plugin).saveConfig();
            verify(p).sendMessage(contains("erfolgreich"));
        }
    }
}
