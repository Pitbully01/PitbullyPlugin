package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.utils.SafeTeleport;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class BackCommandTest {

    @Test
    void nonPlayerSenderGetsMessage() {
        CommandSender sender = mock(CommandSender.class);
        BackCommand cmd = new BackCommand();
        boolean handled = cmd.onCommand(sender, mock(Command.class), "back", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void playerNoLastLocationShowsHelp() {
        Player player = mock(Player.class);
        when(player.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(id);

        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
             MockedStatic<SafeTeleport> st = mockStatic(SafeTeleport.class)) {
            lm.when(() -> LocationManager.checkLastLocation(id)).thenReturn(false);

            BackCommand cmd = new BackCommand();
            boolean handled = cmd.onCommand(player, mock(Command.class), "back", new String[]{});
            assertThat(handled).isTrue();
            verify(player).sendMessage(contains("Es gibt keinen Weg zurück"));
        }
    }

    @Test
    void playerBackTeleportsWhenLastLocationExists() {
        Player player = mock(Player.class);
        when(player.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(id);

        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        Location last = new Location(world, 1, 64, 1);

        try (MockedStatic<LocationManager> lm = mockStatic(LocationManager.class);
             MockedStatic<SafeTeleport> st = mockStatic(SafeTeleport.class)) {
            lm.when(() -> LocationManager.checkLastLocation(id)).thenReturn(true);
            lm.when(() -> LocationManager.getLastLocation(id)).thenReturn(last);
            st.when(() -> SafeTeleport.teleport(player, last)).thenReturn(true);

            BackCommand cmd = new BackCommand();
            boolean handled = cmd.onCommand(player, mock(Command.class), "back", new String[]{});
            assertThat(handled).isTrue();
            verify(player).sendMessage(contains("Zurück teleportiert"));
        }
    }
}
