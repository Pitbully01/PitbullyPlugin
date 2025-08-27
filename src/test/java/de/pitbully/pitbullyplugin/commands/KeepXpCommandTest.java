package de.pitbully.pitbullyplugin.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.storage.LocationStorage;
import de.pitbully.pitbullyplugin.utils.PlayerData;

class KeepXpCommandTest {

    @Mock
    private Player player;
    @Mock
    private Player targetPlayer;
    @Mock
    private Command command;
    @Mock
    private LocationStorage locationStorage;
    @Mock
    private CommandSender consoleSender;

    private KeepXpCommand keepXpCommand;
    private UUID playerId;
    private UUID targetPlayerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        keepXpCommand = new KeepXpCommand();
        
        playerId = UUID.randomUUID();
        targetPlayerId = UUID.randomUUID();
        
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.getName()).thenReturn("TestPlayer");
        when(targetPlayer.getUniqueId()).thenReturn(targetPlayerId);
        when(targetPlayer.getName()).thenReturn("TargetPlayer");
    }

    @Test
    void testConsoleUsage() {
        // Console sender should be rejected
        boolean result = keepXpCommand.onCommand(consoleSender, command, "keepxp", new String[]{});
        
        assertThat(result).isTrue();
        verify(consoleSender).sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
    }

    @Test
    void testNoPermission() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(false);
        
        boolean result = keepXpCommand.onCommand(player, command, "keepxp", new String[]{});
        
        assertThat(result).isTrue();
        verify(player).sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
    }

    @Test
    void testTooManyArguments() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
            new String[]{"on", "player", "extra"});
        
        assertThat(result).isTrue();
        verify(player).sendMessage("§cFehler: Zu viele Argumente!");
        verify(player).sendMessage("§eVerwendung: /keepxp [on|off] [player]");
    }

    @Test
    void testShowCurrentStatus() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        try (MockedStatic<LocationStorage> locationStorageMock = mockStatic(LocationStorage.class)) {
            locationStorageMock.when(() -> LocationStorage.isKeepingXp(player)).thenReturn(true);
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", new String[]{});
            
            assertThat(result).isTrue();
            verify(player).sendMessage("§eAktuell wird deine XP beim Sterben behalten.");
        }
    }

    @Test
    void testShowCurrentStatusNotKeeping() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        try (MockedStatic<LocationStorage> locationStorageMock = mockStatic(LocationStorage.class)) {
            locationStorageMock.when(() -> LocationStorage.isKeepingXp(player)).thenReturn(false);
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", new String[]{});
            
            assertThat(result).isTrue();
            verify(player).sendMessage("§eAktuell wird deine XP beim Sterben nicht behalten.");
        }
    }

    @Test
    void testEnableKeepXp() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        PlayerData playerData = new PlayerData();
        playerData.setKeepXp(false); // Currently disabled
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(playerId)).thenReturn(playerData);
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
                new String[]{"on"});
            
            assertThat(result).isTrue();
            assertThat(playerData.isKeepXp()).isTrue(); // Should be enabled now
            verify(locationStorage).savePlayerData(eq(playerId), eq(playerData));
            verify(player).sendMessage("§a§lKeep XP aktiviert! Du wirst deine XP beim Sterben behalten.");
        }
    }

    @Test
    void testDisableKeepXp() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        PlayerData playerData = new PlayerData();
        playerData.setKeepXp(true); // Currently enabled
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(playerId)).thenReturn(playerData);
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
                new String[]{"off"});
            
            assertThat(result).isTrue();
            assertThat(playerData.isKeepXp()).isFalse(); // Should be disabled now
            verify(locationStorage).savePlayerData(eq(playerId), eq(playerData));
            verify(player).sendMessage("§c§lKeep XP deaktiviert! Du wirst nicht deine XP beim Sterben behalten.");
        }
    }

    @Test
    void testTargetOtherPlayerWithoutPermission() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        when(player.hasPermission("pitbullyplugin.keepxp.others")).thenReturn(false);
        
        boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
            new String[]{"on", "TargetPlayer"});
        
        assertThat(result).isTrue();
        verify(player).sendMessage("§cDu hast keine Berechtigung, KeepXP für andere Spieler zu ändern!");
    }

    @Test
    void testTargetOfflinePlayer() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        when(player.hasPermission("pitbullyplugin.keepxp.others")).thenReturn(true);
        
        try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class)) {
            bukkitMock.when(() -> Bukkit.getPlayer("OfflinePlayer")).thenReturn(null);
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
                new String[]{"on", "OfflinePlayer"});
            
            assertThat(result).isTrue();
            verify(player).sendMessage("§cSpieler 'OfflinePlayer' ist nicht online!");
        }
    }

    @Test
    void testTargetOtherPlayerSuccess() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        when(player.hasPermission("pitbullyplugin.keepxp.others")).thenReturn(true);
        
        PlayerData targetData = new PlayerData();
        targetData.setKeepXp(false);
        
        try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);
             MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            
            bukkitMock.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(targetPlayerId)).thenReturn(targetData);
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
                new String[]{"on", "TargetPlayer"});
            
            assertThat(result).isTrue();
            assertThat(targetData.isKeepXp()).isTrue(); // Should be enabled now
            verify(locationStorage).savePlayerData(eq(targetPlayerId), eq(targetData));
            verify(player).sendMessage("§a§lKeep XP für TargetPlayer aktiviert!");
            verify(targetPlayer).sendMessage("§a§lDein Keep XP wurde aktiviert!");
        }
    }

    @Test
    void testUnknownAction() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
            new String[]{"invalid"});
        
        assertThat(result).isTrue();
        verify(player).sendMessage("§cUnbekannte Aktion: invalid");
        verify(player).sendMessage("§eVerwendung: /keepxp [on|off] [player]");
    }

    @Test
    void testWithNullPlayerData() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(playerId)).thenReturn(null); // No existing data
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
                new String[]{"on"});
            
            assertThat(result).isTrue();
            // Should create new PlayerData with keepXp=true and save it
            verify(locationStorage).savePlayerData(eq(playerId), argThat(data -> 
                data instanceof PlayerData && ((PlayerData) data).isKeepXp() == true));
            verify(player).sendMessage("§a§lKeep XP aktiviert! Du wirst deine XP beim Sterben behalten.");
        }
    }

    @Test
    void testWithNullPlayerDataDisableCommand() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(playerId)).thenReturn(null); // No existing data
            
            boolean result = keepXpCommand.onCommand(player, command, "keepxp", 
                new String[]{"off"});
            
            assertThat(result).isTrue();
            // Should create new PlayerData with keepXp=false and save it
            verify(locationStorage).savePlayerData(eq(playerId), argThat(data -> 
                data instanceof PlayerData && ((PlayerData) data).isKeepXp() == false));
            verify(player).sendMessage("§c§lKeep XP deaktiviert! Du wirst nicht deine XP beim Sterben behalten.");
        }
    }

    @Test
    void testToggleFromFalseToTrue() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        PlayerData playerData = new PlayerData();
        playerData.setKeepXp(false);
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(playerId)).thenReturn(playerData);
            
            keepXpCommand.onCommand(player, command, "keepxp", new String[]{"on"});
            
            // Verify the data was toggled correctly
            verify(locationStorage).savePlayerData(eq(playerId), argThat(data -> 
                data.isKeepXp() == true
            ));
        }
    }

    @Test
    void testNoToggleWhenAlreadyCorrectState() {
        when(player.hasPermission("pitbullyplugin.keepxp")).thenReturn(true);
        
        PlayerData playerData = new PlayerData();
        playerData.setKeepXp(true); // Already enabled
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class)) {
            locationManagerMock.when(LocationManager::getStorage).thenReturn(locationStorage);
            when(locationStorage.getPlayerData(playerId)).thenReturn(playerData);
            
            keepXpCommand.onCommand(player, command, "keepxp", new String[]{"on"});
            
            // Should save without toggling (state remains true)
            verify(locationStorage).savePlayerData(eq(playerId), argThat(data -> 
                data.isKeepXp() == true
            ));
        }
    }
}
