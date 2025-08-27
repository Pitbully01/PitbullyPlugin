package de.pitbully.pitbullyplugin.listeners;

import static org.mockito.Mockito.*;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import de.pitbully.pitbullyplugin.storage.LocationManager;
import de.pitbully.pitbullyplugin.storage.LocationStorage;

class PlayerDeathListenerTest {
    
    @Mock
    private Player player;
    
    @Mock
    private World world;
    
    @Mock
    private PlayerDeathEvent deathEvent;
    
    private PlayerDeathListener listener;
    private UUID playerId;
    private Location deathLocation;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new PlayerDeathListener();
        
        playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.getName()).thenReturn("TestPlayer");
        
        when(world.getName()).thenReturn("world");
        deathLocation = new Location(world, 100, 64, 200);
        when(player.getLocation()).thenReturn(deathLocation);
        
        when(deathEvent.getEntity()).thenReturn(player);
        when(player.getTotalExperience()).thenReturn(1000);
    }
    
    @Test
    void testDeathLocationIsSaved() {
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class);
             MockedStatic<LocationStorage> locationStorageMock = mockStatic(LocationStorage.class)) {
            
            locationStorageMock.when(() -> LocationStorage.isKeepingXp(player)).thenReturn(false);
            
            listener.onPlayerDeath(deathEvent);
            
            // Verify death location is saved
            locationManagerMock.verify(() -> LocationManager.updateLastDeathLocations(playerId, deathLocation));
        }
    }
    
    @Test
    void testKeepXpEnabled() {
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class);
             MockedStatic<LocationStorage> locationStorageMock = mockStatic(LocationStorage.class)) {
            
            locationStorageMock.when(() -> LocationStorage.isKeepingXp(player)).thenReturn(true);
            
            listener.onPlayerDeath(deathEvent);
            
            // Verify death location is saved
            locationManagerMock.verify(() -> LocationManager.updateLastDeathLocations(playerId, deathLocation));
            
            // Verify experience is preserved
            verify(deathEvent).setKeepLevel(true);
            verify(deathEvent).setDroppedExp(0);
            verify(player).setTotalExperience(1000);
        }
    }
    
    @Test
    void testKeepXpDisabled() {
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class);
             MockedStatic<LocationStorage> locationStorageMock = mockStatic(LocationStorage.class)) {
            
            locationStorageMock.when(() -> LocationStorage.isKeepingXp(player)).thenReturn(false);
            
            listener.onPlayerDeath(deathEvent);
            
            // Verify death location is saved
            locationManagerMock.verify(() -> LocationManager.updateLastDeathLocations(playerId, deathLocation));
            
            // Verify experience is NOT preserved
            verify(deathEvent, never()).setKeepLevel(anyBoolean());
            verify(deathEvent, never()).setDroppedExp(anyInt());
            verify(player, never()).setTotalExperience(anyInt());
        }
    }
    
    @Test
    void testZeroExperienceHandling() {
        when(player.getTotalExperience()).thenReturn(0);
        
        try (MockedStatic<LocationManager> locationManagerMock = mockStatic(LocationManager.class);
             MockedStatic<LocationStorage> locationStorageMock = mockStatic(LocationStorage.class)) {
            
            locationStorageMock.when(() -> LocationStorage.isKeepingXp(player)).thenReturn(true);
            
            listener.onPlayerDeath(deathEvent);
            
            // Should still preserve experience even if it's zero
            verify(deathEvent).setKeepLevel(true);
            verify(deathEvent).setDroppedExp(0);
            verify(player).setTotalExperience(0);
        }
    }
}
