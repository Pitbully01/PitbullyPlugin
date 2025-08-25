package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.Test;

class EnderchestCommandTest {

    @Test
    void nonPlayer() {
        var sender = mock(org.bukkit.command.CommandSender.class);
        boolean handled = new EnderchestCommand().onCommand(sender, mock(Command.class), "enderchest", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("nur von Spielern"));
    }

    @Test
    void selfNoPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission("pitbullyplugin.enderchest")).thenReturn(false);
        boolean handled = new EnderchestCommand().onCommand(p, mock(Command.class), "enderchest", new String[]{});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void opensOwnEnderchest() {
        Player p = mock(Player.class);
        when(p.hasPermission("pitbullyplugin.enderchest")).thenReturn(true);
    Inventory inv = mock(Inventory.class);
    when(p.getEnderChest()).thenReturn(inv);
        boolean handled = new EnderchestCommand().onCommand(p, mock(Command.class), "enderchest", new String[]{});
        assertThat(handled).isTrue();
    verify(p).openInventory(inv);
    verify(p).sendMessage(contains("geöffnet"));
    }

    @Test
    void othersNoPermission() {
        Player p = mock(Player.class);
        when(p.hasPermission("pitbullyplugin.enderchest.others")).thenReturn(false);
        boolean handled = new EnderchestCommand().onCommand(p, mock(Command.class), "enderchest", new String[]{"Other"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void othersNotFound() {
        Player p = mock(Player.class);
        when(p.hasPermission("pitbullyplugin.enderchest.others")).thenReturn(true);
        Server server = mock(Server.class);
        when(p.getServer()).thenReturn(server);
        when(server.getPlayer("Other")).thenReturn(null);
        boolean handled = new EnderchestCommand().onCommand(p, mock(Command.class), "enderchest", new String[]{"Other"});
        assertThat(handled).isTrue();
        verify(p).sendMessage(contains("wurde nicht gefunden"));
    }

    @Test
    void opensOthersEnderchest() {
        Player p = mock(Player.class);
        when(p.hasPermission("pitbullyplugin.enderchest.others")).thenReturn(true);
        Server server = mock(Server.class);
        when(p.getServer()).thenReturn(server);
        Player target = mock(Player.class);
        when(server.getPlayer("Other")).thenReturn(target);
        Inventory inv = mock(Inventory.class);
        when(target.getEnderChest()).thenReturn(inv);
        when(target.getName()).thenReturn("Other");
        boolean handled = new EnderchestCommand().onCommand(p, mock(Command.class), "enderchest", new String[]{"Other"});
        assertThat(handled).isTrue();
        verify(p).openInventory(inv);
        verify(p).sendMessage(contains("geöffnet"));
    }
}
