package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.utils.TpaRequestManager;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TpaHereCommandTest {

    @Test
    void nonPlayer() {
        CommandSender sender = mock(CommandSender.class);
        boolean handled = new TpaHereCommand().onCommand(sender, mock(Command.class), "tpahere", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new TpaHereCommand().onCommand(p, mock(Command.class), "tpahere", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void wrongArgs() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        boolean handled = new TpaHereCommand().onCommand(p, mock(Command.class), "tpahere", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("benötigt genau ein Argument"));
        verify(p).sendMessage(contains("/tpahere <Spieler>"));
    }

    @Test
    void selfTargetNotAllowed() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        when(p.getName()).thenReturn("Alice");
        boolean handled = new TpaHereCommand().onCommand(p, mock(Command.class), "tpahere", new String[]{"Alice"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("nicht zu dir selbst"));
    }

    @Test
    void targetNotFoundOrOffline() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        Server s = mock(Server.class);
        when(p.getServer()).thenReturn(s);
        when(s.getPlayer("Bob")).thenReturn(null);

        boolean handled = new TpaHereCommand().onCommand(p, mock(Command.class), "tpahere", new String[]{"Bob"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("ist nicht online"));
    }

    @Test
    void sendsRequestAndConfirmsToSender() {
        Player requester = mock(Player.class);
        when(requester.hasPermission(anyString())).thenReturn(true);
        when(requester.getName()).thenReturn("Alice");
        UUID rid = UUID.randomUUID();
        when(requester.getUniqueId()).thenReturn(rid);

        Player target = mock(Player.class);
        when(target.getName()).thenReturn("Bob");
        when(target.isOnline()).thenReturn(true);
        UUID tid = UUID.randomUUID();
        when(target.getUniqueId()).thenReturn(tid);

        Server s = mock(Server.class);
        when(requester.getServer()).thenReturn(s);
        when(s.getPlayer("Bob")).thenReturn(target);

        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            // Force early return in sendRequest to avoid scheduler
            mgr.when(() -> TpaRequestManager.hasOutgoing(tid)).thenReturn(true);
            boolean handled = new TpaHereCommand().onCommand(requester, mock(Command.class), "tpahere", new String[]{"Bob"});
            assertThat(handled).isTrue();
            verify(requester).sendMessage(contains("gesendet"));
        }
    }
}
