package de.pitbully.pitbullyplugin;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {
    private final PitbullyPlugin plugin;

    public Config(PitbullyPlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = this.plugin.getConfig();

        config.addDefault("bStats.enabled", true);
        config.setComments("bStats.enabled", List.of(
                "Enable bStats for this plugin"
        ));

        config.addDefault("tpa.timeout", 60);
        config.setComments("tpa.timeout", List.of("Time in seconds you have to accept a teleport request"));

        config.addDefault("tpa.cooldowns.tpa", 60);
        config.setComments("tpa.cooldowns.tpa", List.of(
                "Time in seconds you have to wait before you can send another teleport request"
        ));

        config.options().copyDefaults(true);
        this.plugin.saveConfig();
        this.plugin.reloadConfig();
    }
    public String getString(String key) {
        return this.plugin.getConfig().getString(key);
    }
    public String getString(String key, String defaultValue) {
        return this.plugin.getConfig().getString(key, defaultValue);
    }

    public int getInt(String key) {
        return this.plugin.getConfig().getInt(key);
    }
    public int getInt(String key, int defaultValue) {
        return this.plugin.getConfig().getInt(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return this.plugin.getConfig().getBoolean(key);
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.plugin.getConfig().getBoolean(key, defaultValue);
    }
}
