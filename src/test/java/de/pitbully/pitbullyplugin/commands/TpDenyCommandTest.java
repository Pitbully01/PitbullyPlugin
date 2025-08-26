package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.utils.TpaRequest;
import de.pitbully.pitbullyplugin.utils.TpaRequestManager;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TpDenyCommandTest {

    @Test
    void nonPlayer() {
        CommandSender sender = mock(CommandSender.class);
        boolean handled = new TpDenyCommand().onCommand(sender, mock(Command.class), "tpdeny", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new TpDenyCommand().onCommand(p, mock(Command.class), "tpdeny", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void noIncomingRequest() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(id);
        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            mgr.when(() -> TpaRequestManager.getIncoming(id)).thenReturn(null);
            boolean handled = new TpDenyCommand().onCommand(p, mock(Command.class), "tpdeny", new String[]{});
            assertThat(handled).isTrue();
            verify(p).sendMessage(contains("keine offene Teleport-Anfrage"));
        }
    }

    @Test
    void deniesAndNotifies() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        UUID id = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(id);

        TpaRequest req = mock(TpaRequest.class);
        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            mgr.when(() -> TpaRequestManager.getIncoming(id)).thenReturn(req);
            boolean handled = new TpDenyCommand().onCommand(p, mock(Command.class), "tpdeny", new String[]{});
            assertThat(handled).isTrue();
            verify(req).denyRequest();
            verify(p).sendMessage(contains("abgelehnt"));
        }
    }
}
