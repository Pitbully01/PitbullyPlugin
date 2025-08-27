package de.pitbully.pitbullyplugin.commands;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für TpaCommand ohne Abhängigkeit von MockBukkit.
 * Testet die Kommando-Logik isoliert mit Mocks.
 */
@ExtendWith(MockitoExtension.class)
class TpaCommandUnitTest {
    
    @Mock
    private Player sender;
    
    @Mock
    private Player target;
    
    @Mock
    private CommandSender consoleSender;
    
    @Mock
    private Command command;
    
    @Mock
    private Server server;
    
    private TpaCommand tpaCommand;
    
    @BeforeEach
    void setUp() {
        tpaCommand = new TpaCommand();
    }
    
    @Test
    void commandRequiresPlayerSender() {
        // Given
        String[] args = {"targetPlayer"};
        
        // When
        boolean result = tpaCommand.onCommand(consoleSender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue(); // TpaCommand gibt immer true zurück
        verify(consoleSender).sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
    }
    
    @Test
    void commandRequiresExactlyOneArgument() {
        // Given - Keine Argumente
        String[] args = {};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cFehler: Dieser Befehl benötigt genau ein Argument!");
        verify(sender).sendMessage("§eVerwendung: /tpa <Spieler>");
    }
    
    @Test
    void commandRequiresExactlyOneArgumentTooMany() {
        // Given - Zu viele Argumente
        String[] args = {"player1", "player2"};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cFehler: Dieser Befehl benötigt genau ein Argument!");
        verify(sender).sendMessage("§eVerwendung: /tpa <Spieler>");
    }
    
    @Test
    void commandChecksPermissions() {
        // Given
        String[] args = {"targetPlayer"};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(false);
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
    }
    
    @Test
    void commandRejectsSelfTeleportation() {
        // Given
        String[] args = {"SenderPlayer"};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        when(sender.getName()).thenReturn("SenderPlayer");
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cFehler: Du kannst dich nicht zu dir selbst teleportieren!");
    }
    
    @Test
    void commandRejectsSelfTeleportationCaseInsensitive() {
        // Given
        String[] args = {"senderplayer"}; // Andere Groß-/Kleinschreibung
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        when(sender.getName()).thenReturn("SenderPlayer");
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cFehler: Du kannst dich nicht zu dir selbst teleportieren!");
    }
    
    @Test
    void commandHandlesOfflinePlayer() {
        // Given
        String[] args = {"OfflinePlayer"};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        when(sender.getName()).thenReturn("SenderPlayer");
        when(sender.getServer()).thenReturn(server);
        when(server.getPlayer("OfflinePlayer")).thenReturn(null);
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cFehler: Spieler 'OfflinePlayer' ist nicht online oder wurde nicht gefunden!");
    }
    
    @Test
    void commandHandlesOfflinePlayerNotOnline() {
        // Given
        String[] args = {"TargetPlayer"};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        when(sender.getName()).thenReturn("SenderPlayer");
        when(sender.getServer()).thenReturn(server);
        when(server.getPlayer("TargetPlayer")).thenReturn(target);
        when(target.isOnline()).thenReturn(false);
        
        // When
        boolean result = tpaCommand.onCommand(sender, command, "tpa", args);
        
        // Then
        assertThat(result).isTrue();
        verify(sender).sendMessage("§cFehler: Spieler 'TargetPlayer' ist nicht online oder wurde nicht gefunden!");
    }
    
    @Test
    void commandValidatesAllInputsCorrectly() {
        // Given
        String[] args = {"TargetPlayer"};
        when(sender.hasPermission("pitbullyplugin.tpa")).thenReturn(true);
        when(sender.getName()).thenReturn("SenderPlayer");
        when(sender.getServer()).thenReturn(server);
        when(server.getPlayer("TargetPlayer")).thenReturn(target);
        when(target.isOnline()).thenReturn(true);
        
        // When - Note: Wir testen nur die Validierung bis zur TpaRequest-Erstellung
        try {
            tpaCommand.onCommand(sender, command, "tpa", args);
            // Falls TpaRequest erfolgreich erstellt wird, schlägt sie wegen fehlendem Scheduler fehl
        } catch (NullPointerException e) {
            // Das ist das erwartete Verhalten - zeigt dass die Validierung erfolgreich war
            assertThat(e.getMessage()).contains("Bukkit.server");
        }
        
        // Das wichtige ist: keine Fehlermeldungen bis zur TpaRequest-Erstellung
        verify(sender, never()).sendMessage(anyString());
    }
}
