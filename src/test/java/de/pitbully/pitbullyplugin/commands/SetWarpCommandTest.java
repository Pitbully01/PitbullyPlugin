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

class SetWarpCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new SetWarpCommand().onCommand(sender, mock(Command.class), "setwarp", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void invalidArgs() {
        Player p = mock(Player.class);
        boolean handled = new SetWarpCommand().onCommand(p, mock(Command.class), "setwarp", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("Warp-Namen"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new SetWarpCommand().onCommand(p, mock(Command.class), "setwarp", new String[]{"spawn"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void setsWarpAndSavesConfig() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        when(p.getLocation()).thenReturn(new Location(w, 1,64,1));

    try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
         MockedStatic<PitbullyPlugin> pl = mockStatic(PitbullyPlugin.class)) {
            lm.when(() -> LocationManager.checkWarpLocation("spawn")).thenReturn(false);
        PitbullyPlugin plugin = mock(PitbullyPlugin.class);
        pl.when(PitbullyPlugin::getInstance).thenReturn(plugin);

            boolean handled = new SetWarpCommand().onCommand(p, mock(Command.class), "setwarp", new String[]{"spawn"});
            assertThat(handled).isTrue();
        lm.verify(() -> LocationManager.updateWarpLocation(eq("spawn"), any(Location.class)));
            verify(plugin).saveConfig();
            verify(p).sendMessage(contains("erfolgreich gesetzt"));
        }
    }
}
