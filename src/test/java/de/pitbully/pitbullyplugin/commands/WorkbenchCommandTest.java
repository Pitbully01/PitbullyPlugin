package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class WorkbenchCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new WorkbenchCommand().onCommand(sender, mock(Command.class), "workbench", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void noPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(false);
        boolean handled = new WorkbenchCommand().onCommand(p, mock(Command.class), "workbench", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void opensWorkbench() {
        Player p = mock(Player.class);
        when(p.hasPermission(anyString())).thenReturn(true);
        boolean handled = new WorkbenchCommand().onCommand(p, mock(Command.class), "workbench", new String[]{});
        assertThat(handled).isTrue();
        verify(p).openWorkbench(null, true);
    }
}
