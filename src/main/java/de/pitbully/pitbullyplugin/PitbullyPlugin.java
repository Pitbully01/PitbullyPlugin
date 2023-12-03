package de.pitbully.pitbullyplugin;

import de.pitbully.pitbullyplugin.commands.*;
import de.pitbully.pitbullyplugin.listeners.LocationListener;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class PitbullyPlugin extends JavaPlugin {
    private static PitbullyPlugin instance;
    private File configFile;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerCommands();
        instance = this;
        getServer().getPluginManager().registerEvents(new LocationListener(), this);

        initConfig();
        loadConfig();
    }

    private void initConfig() {
        if (config == null) {
            configFile = new File(getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(configFile);
        }

    }

    public static PitbullyPlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    private void registerCommands() {
        registerCommand("home", new HomeCommand());
        registerCommand("sethome", new SetHomeCommand());
        registerCommand("delhome", new DeleteHomeCommand());
        registerCommand("back", new BackCommand());
        ;

    }

    private void registerCommand(String command, CommandExecutor CommandExecutor) {
        Objects.requireNonNull(getCommand(command)).setExecutor(CommandExecutor);
    }

    private void loadConfig() {
        if (config == null) {
            getLogger().warning("Config not initialized. Skipping load.");
            return;
        }

        if (!configFile.exists() || configFile.length() == 0) {
            getLogger().warning("Config.yml not found or empty. Using default values.");
            return;
        }

        // Load locations from config
        Locations.loadFromConfig(config);
    }

    public void saveConfig() {
        if (config == null) {
            getLogger().warning("Config not initialized. Skipping save.");
            return;
        }

        // Save locations to config
        Locations.saveToConfig(config);

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
