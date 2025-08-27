package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.utils.SafeTeleport;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class WarpCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new WarpCommand().onCommand(sender, mock(Command.class), "warp", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void invalidArgs() {
        Player p = mock(Player.class);
        boolean handled = new WarpCommand().onCommand(p, mock(Command.class), "warp", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("Warp-Namen"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new WarpCommand().onCommand(p, mock(Command.class), "warp", new String[]{"spawn"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void warpDoesNotExist() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class)) {
            lm.when(() -> LocationManager.checkWarpLocation("spawn")).thenReturn(false);
            boolean handled = new WarpCommand().onCommand(p, mock(Command.class), "warp", new String[]{"spawn"});
            assertThat(handled).isTrue();
            verify(p).sendMessage(contains("existiert nicht"));
        }
    }

    @Test
    void warpsSuccessfully() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        Location loc = new Location(w, 1,64,1);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
             MockedStatic<SafeTeleport> st = mockStatic(SafeTeleport.class)) {
            lm.when(() -> LocationManager.checkWarpLocation("spawn")).thenReturn(true);
            lm.when(() -> LocationManager.getWarpLocation("spawn")).thenReturn(loc);
            st.when(() -> SafeTeleport.teleport(p, loc)).thenReturn(true);
            boolean handled = new WarpCommand().onCommand(p, mock(Command.class), "warp", new String[]{"spawn"});
            assertThat(handled).isTrue();
            
            // Verify the actual teleportation was attempted
            st.verify(() -> SafeTeleport.teleport(p, loc));
            // Verify success message
            verify(p).sendMessage(contains("teleportiert"));
        }
    }

    @Test
    void warpFailsWhenTeleportFails() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        Location loc = new Location(w, 1,64,1);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
             MockedStatic<SafeTeleport> st = mockStatic(SafeTeleport.class)) {
            lm.when(() -> LocationManager.checkWarpLocation("spawn")).thenReturn(true);
            lm.when(() -> LocationManager.getWarpLocation("spawn")).thenReturn(loc);
            st.when(() -> SafeTeleport.teleport(p, loc)).thenReturn(false); // Teleport fails
            boolean handled = new WarpCommand().onCommand(p, mock(Command.class), "warp", new String[]{"spawn"});
            assertThat(handled).isTrue();
            
            // Should still attempt teleport
            st.verify(() -> SafeTeleport.teleport(p, loc));
            // Should NOT send success message when teleport fails
            verify(p, never()).sendMessage(contains("teleportiert"));
        }
    }

    @Test
    void testWarpNameIsPassedCorrectly() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class)) {
            lm.when(() -> LocationManager.checkWarpLocation("customwarp")).thenReturn(false);
            boolean handled = new WarpCommand().onCommand(p, mock(Command.class), "warp", new String[]{"customwarp"});
            assertThat(handled).isTrue();
            
            // Verify correct warp name was checked
            lm.verify(() -> LocationManager.checkWarpLocation("customwarp"));
            verify(p).sendMessage(contains("existiert nicht"));
        }
    }
}
