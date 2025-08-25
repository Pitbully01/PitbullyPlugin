package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.utils.SafeTeleport;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class HomeCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new HomeCommand().onCommand(sender, mock(Command.class), "home", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new HomeCommand().onCommand(p, mock(Command.class), "home", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void noHomeSet() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(id);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class)) {
            lm.when(() -> LocationManager.checkHomeLocation(id)).thenReturn(false);
            boolean handled = new HomeCommand().onCommand(p, mock(Command.class), "home", new String[]{});
            assertThat(handled).isTrue();
            verify(p).sendMessage(contains("Kein Home gesetzt"));
        }
    }

    @Test
    void teleportsToHome() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(id);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        Location home = new Location(w, 1, 64, 1);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
             MockedStatic<SafeTeleport> st = mockStatic(SafeTeleport.class)) {
            lm.when(() -> LocationManager.checkHomeLocation(id)).thenReturn(true);
            lm.when(() -> LocationManager.getHomeLocation(id)).thenReturn(home);
            st.when(() -> SafeTeleport.teleport(p, home)).thenReturn(true);
            boolean handled = new HomeCommand().onCommand(p, mock(Command.class), "home", new String[]{});
            assertThat(handled).isTrue();
            verify(p).sendMessage(contains("Hause"));
        }
    }
}
