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
    private boolean keepXp = true; // Default to true for new players

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
        // PlayerData is considered empty only if all data is at default values
        // This means all locations are null AND keepXp is true (the default)
        return lastDeath == null && lastTeleport == null && lastLocation == null && home == null && keepXp == true;
    }
    public boolean isKeepXp() {
        return keepXp;
    }

    public void setKeepXp(boolean keepXp) {
        this.keepXp = keepXp;
    }

    public void toggleSetKeepXp() {
        this.keepXp = !keepXp;
    }

    /**
     * Writes this PlayerData into the provided configuration section.
     * Keys: lastDeath, lastTeleport, lastLocation, home, keepXp
     */
    public void toConfig(ConfigurationSection section) {
        if (section == null) return;
        section.set("lastDeath", lastDeath);
        section.set("lastTeleport", lastTeleport);
        section.set("lastLocation", lastLocation);
        section.set("home", home);
        section.set("keepXp", keepXp);
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
        Object keepXp = section.get("keepXp");
        if (death instanceof Location) data.setLastDeath((Location) death);
        if (teleport instanceof Location) data.setLastTeleport((Location) teleport);
        if (last instanceof Location) data.setLastLocation((Location) last);
        if (home instanceof Location) data.setHome((Location) home);
        if (keepXp instanceof Boolean) data.setKeepXp((Boolean) keepXp);
        else data.setKeepXp(true); // Default to true for new players
        return data;
    }
}
