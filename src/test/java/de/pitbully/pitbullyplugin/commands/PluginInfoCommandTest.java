package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

class PluginInfoCommandTest {

    @Test
    void noPermission() {
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission(anyString())).thenReturn(false);
        boolean handled = new PluginInfoCommand().onCommand(sender, mock(Command.class), "pitbullyinfo", new String[]{});
        assertThat(handled).isTrue();
        verify(sender).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void printsInfo() {
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission(anyString())).thenReturn(true);
        boolean handled = new PluginInfoCommand().onCommand(sender, mock(Command.class), "pitbullyinfo", new String[]{});
        assertThat(handled).isTrue();
        verify(sender, atLeastOnce()).sendMessage(contains("Information"));
    }
}
