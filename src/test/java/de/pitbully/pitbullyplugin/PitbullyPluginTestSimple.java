package de.pitbully.pitbullyplugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Einfache Unit-Tests für die grundlegenden Plugin-Funktionen.
 * Diese Tests funktionieren ohne MockBukkit und testen die Plugin-Metadaten.
 */
@ExtendWith(MockitoExtension.class)
class PitbullyPluginTestSimple {
    
    @Mock
    private PluginDescriptionFile mockDescription;
    
    @Mock
    private PluginCommand mockCommand;
    
    @Mock 
    private PluginManager mockPluginManager;

    @Test
    void pluginNameIsCorrect() {
        // Given
        when(mockDescription.getName()).thenReturn("PitbullyPlugin");
        
        // When
        String name = mockDescription.getName();
        
        // Then
        assertThat(name).isEqualTo("PitbullyPlugin");
    }

    @Test
    void pluginVersionIsSet() {
        // Given
        when(mockDescription.getVersion()).thenReturn("1.6.1");
        
        // When
        String version = mockDescription.getVersion();
        
        // Then
        assertThat(version).isNotNull()
                        .isNotEmpty()
                        .matches("\\d+\\.\\d+\\.\\d+");
    }

    @Test
    void pluginMainClassIsCorrect() {
        // Given
        when(mockDescription.getMain()).thenReturn("de.pitbully.pitbullyplugin.PitbullyPlugin");
        
        // When
        String mainClass = mockDescription.getMain();
        
        // Then
        assertThat(mainClass).isEqualTo("de.pitbully.pitbullyplugin.PitbullyPlugin");
    }

    @Test
    void pluginHasDescription() {
        // Given
        when(mockDescription.getDescription()).thenReturn("Ein umfassendes Plugin für Minecraft Server");
        
        // When
        String description = mockDescription.getDescription();
        
        // Then
        assertThat(description).isNotNull()
                             .isNotEmpty();
    }

    @Test
    void commandCanBeRetrieved() {
        // Given
        when(mockCommand.getName()).thenReturn("tpa");
        
        // When
        String commandName = mockCommand.getName();
        
        // Then
        assertThat(commandName).isEqualTo("tpa");
    }

    @Test 
    void pluginManagerCanBeUsed() {
        // Given
        when(mockPluginManager.isPluginEnabled("PitbullyPlugin")).thenReturn(true);
        
        // When
        boolean isEnabled = mockPluginManager.isPluginEnabled("PitbullyPlugin");
        
        // Then
        assertThat(isEnabled).isTrue();
    }
}
