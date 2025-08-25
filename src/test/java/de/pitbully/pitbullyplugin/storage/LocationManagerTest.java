package de.pitbully.pitbullyplugin.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LocationManagerTest {

    static class FakeStorage implements LocationStorage {
        Map<UUID, Location> deaths = new HashMap<>();
        Map<UUID, Location> teleports = new HashMap<>();
        Map<UUID, Location> lasts = new HashMap<>();
        Map<UUID, Location> homes = new HashMap<>();
        Map<String, Location> warps = new HashMap<>();
        Location spawn;

        public void saveDeathLocation(UUID playerId, Location location) { deaths.put(playerId, location); }
        public Location getDeathLocation(UUID playerId) { return deaths.get(playerId); }
        public Map<UUID, Location> getAllDeathLocations() { return deaths; }
        public void saveTeleportLocation(UUID playerId, Location location) { teleports.put(playerId, location); }
        public Location getTeleportLocation(UUID playerId) { return teleports.get(playerId); }
        public Map<UUID, Location> getAllTeleportLocations() { return teleports; }
        public void saveLastLocation(UUID playerId, Location location) { lasts.put(playerId, location); }
        public Location getLastLocation(UUID playerId) { return lasts.get(playerId); }
        public Map<UUID, Location> getAllLastLocations() { return lasts; }
        public void saveHomeLocation(UUID playerId, Location location) { homes.put(playerId, location); }
        public Location getHomeLocation(UUID playerId) { return homes.get(playerId); }
        public boolean hasHomeLocation(UUID playerId) { return homes.containsKey(playerId); }
        public void deleteHomeLocation(UUID playerId) { homes.remove(playerId); }
        public Map<UUID, Location> getAllHomeLocations() { return homes; }
        public void saveWarpLocation(String warpName, Location location) { warps.put(warpName, location); }
        public Location getWarpLocation(String warpName) { return warps.get(warpName); }
        public boolean hasWarpLocation(String warpName) { return warps.containsKey(warpName); }
        public void deleteWarpLocation(String warpName) { warps.remove(warpName); }
        public Map<String, Location> getAllWarpLocations() { return warps; }
        public void saveWorldSpawn(Location location) { spawn = location; }
        public Location getWorldSpawn() { return spawn; }
        public void loadAll() {}
        public void saveAll() {}
        public void close() {}
        public boolean hasLastDeathLocation(UUID uniqueId) { return deaths.containsKey(uniqueId); }
        public boolean hasLastTeleportLocation(UUID uniqueId) { return teleports.containsKey(uniqueId); }
        public Location getLastTeleportLocation(UUID uniqueId) { return teleports.get(uniqueId); }
    }

    World world;

    @BeforeEach
    void init() {
        world = Mockito.mock(World.class);
        Mockito.when(world.getName()).thenReturn("world");
        LocationManager.initialize(new FakeStorage());
    }

    @Test
    void updateAndGetLastLocation() {
        UUID id = UUID.randomUUID();
        Location loc = new Location(world, 1, 2, 3);
        LocationManager.updateLastLocations(id, loc);
        assertThat(LocationManager.getLastLocation(id)).isEqualTo(loc);
        assertThat(LocationManager.checkLastLocation(id)).isTrue();
    }

    @Test
    void homeLifecycle() {
        UUID id = UUID.randomUUID();
        Location home = new Location(world, 5, 65, 5);
        LocationManager.updateHomeLocation(id, home);
        assertThat(LocationManager.checkHomeLocation(id)).isTrue();
        assertThat(LocationManager.getHomeLocation(id)).isEqualTo(home);
        LocationManager.deleteHomeLocation(id);
        assertThat(LocationManager.checkHomeLocation(id)).isFalse();
    }

    @Test
    void warpLifecycle() {
        Location warp = new Location(world, 100, 64, -20);
        LocationManager.updateWarpLocation("spawn", warp);
        assertThat(LocationManager.checkWarpLocation("spawn")).isTrue();
        assertThat(LocationManager.getWarpLocation("spawn")).isEqualTo(warp);
        assertThat(LocationManager.getWarpHashMap()).containsEntry("spawn", warp);
        LocationManager.deleteWarpLocation("spawn");
        assertThat(LocationManager.checkWarpLocation("spawn")).isFalse();
    }
}
