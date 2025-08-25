package de.pitbully.pitbullyplugin.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PluginInfoTest {

    @BeforeAll
    static void setup() {
        // Initialize with a no-op logger; properties file is on classpath (src/main/resources)
        PluginInfo.initialize(Logger.getLogger("test"));
    }

    @Test
    void versionAndNameHaveFallbacks() {
        assertThat(PluginInfo.getName()).isNotBlank();
        assertThat(PluginInfo.getVersion()).isNotBlank();
    }

    @Test
    void fullVersionInfoContainsNameAndVersion() {
        String info = PluginInfo.getFullVersionInfo();
        assertThat(info).contains(PluginInfo.getName());
        assertThat(info).contains("v" + PluginInfo.getVersion());
    }
}
