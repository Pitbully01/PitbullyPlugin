package de.pitbully.pitbullyplugin.utils;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Simple container for per-player persisted data (locations, etc.).
 *
 * <p>Currently tracks:
 * - lastDeath
 * - lastTeleport
 * - lastLocation
 * - home
 *
 * <p>Can be extended later (e.g., settings) and optionally stored in
 * per-player config files without changing the rest of the plugin.
 */
public class PlayerData {

    private Location lastDeath;
    private Location lastTeleport;
    private Location lastLocation;
    private Location home;

    public Location getLastDeath() {
        return lastDeath;
    }

    public void setLastDeath(Location lastDeath) {
        this.lastDeath = lastDeath;
    }

    public Location getLastTeleport() {
        return lastTeleport;
    }

    public void setLastTeleport(Location lastTeleport) {
        this.lastTeleport = lastTeleport;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public boolean isEmpty() {
        return lastDeath == null && lastTeleport == null && lastLocation == null && home == null;
    }

    /**
     * Writes this PlayerData into the provided configuration section.
     * Keys: lastDeath, lastTeleport, lastLocation, home
     */
    public void toConfig(ConfigurationSection section) {
        if (section == null) return;
        section.set("lastDeath", lastDeath);
        section.set("lastTeleport", lastTeleport);
        section.set("lastLocation", lastLocation);
        section.set("home", home);
    }

    /**
     * Reads PlayerData from a configuration section.
     */
    public static PlayerData fromConfig(ConfigurationSection section) {
        if (section == null) return new PlayerData();
        PlayerData data = new PlayerData();
        Object death = section.get("lastDeath");
        Object teleport = section.get("lastTeleport");
        Object last = section.get("lastLocation");
        Object home = section.get("home");
        if (death instanceof Location) data.setLastDeath((Location) death);
        if (teleport instanceof Location) data.setLastTeleport((Location) teleport);
        if (last instanceof Location) data.setLastLocation((Location) last);
        if (home instanceof Location) data.setHome((Location) home);
        return data;
    }
}
