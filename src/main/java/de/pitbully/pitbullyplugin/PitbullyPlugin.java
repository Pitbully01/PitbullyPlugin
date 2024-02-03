package de.pitbully.pitbullyplugin;
/*TODO:/wb workbench, /tpa
*/
import de.pitbully.pitbullyplugin.commands.*;
import de.pitbully.pitbullyplugin.commands.TabCompleters.WarpTabCompleter;
import de.pitbully.pitbullyplugin.listeners.LocationListener;
import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
/**
 * Main class for the PitbullyPlugin Bukkit plugin.
 * This class extends JavaPlugin and serves as the entry point for the plugin.
 *
 * @author Pitbully01
 */
public final class PitbullyPlugin extends JavaPlugin {
    private static PitbullyPlugin instance;
    private File configFile;
    private FileConfiguration config;

    /**
     * Plugin startup logic.
     * Registers commands, initializes configuration, and loads configuration from file.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        registerCommands();
        instance = this;
        getServer().getPluginManager().registerEvents(new LocationListener(), this);

        initConfig();
        loadConfig();
    }

    /**
     * Initializes the plugin configuration if it is not already initialized.
     * This method is called during the plugin startup logic.
     */
    private void initConfig() {
        if (config == null) {
            configFile = new File(getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(configFile);
        }

    }

    /**
     * Retrieves the instance of the PitbullyPlugin.
     *
     * @return The instance of the PitbullyPlugin.
     */
    public static PitbullyPlugin getInstance() {
        return instance;
    }

    /**
     * Plugin shutdown logic.
     * Saves the configuration to file.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    private void registerCommands() {
        registerCommand("home", new HomeCommand());
        registerCommand("sethome", new SetHomeCommand());
        registerCommand("delhome", new DelHomeCommand());
        registerCommand("back", new BackCommand());
        registerCommand("setwarp", new SetWarpCommand());
        registerCommand("warp", new WarpCommand(), new WarpTabCompleter());
        registerCommand("delwarp", new DelWarpCommand(), new WarpTabCompleter());
        registerCommand("enderchest", new EnderchestCommand());
        registerCommand("workbench", new WorkbenchCommand());

    }

    private void registerCommand(String command, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(command)).setExecutor(commandExecutor);
    }

    private void registerCommand(String command, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
        Objects.requireNonNull(getCommand(command)).setTabCompleter(tabCompleter);
        registerCommand(command, commandExecutor);
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
