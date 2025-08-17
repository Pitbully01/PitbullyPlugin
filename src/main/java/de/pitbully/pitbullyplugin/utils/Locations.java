/*     */ package de.pitbully.pitbullyplugin.utils;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Locations
/*     */ {
/*  14 */   private static HashMap<UUID, Location> lastDeathLocations = new HashMap<>();
/*  15 */   private static HashMap<UUID, Location> lastTeleportLocations = new HashMap<>();
/*  16 */   private static HashMap<UUID, Location> lastLocations = new HashMap<>();
/*  17 */   private static HashMap<UUID, Location> homeLocations = new HashMap<>();
/*  18 */   private static HashMap<String, Location> warpLocations = new HashMap<>();
/*     */ 
/*     */   
/*     */   public static void updateLastDeathLocations(UUID playerId, Location location) {
/*  22 */     lastDeathLocations.put(playerId, location);
/*  23 */     updateLastLocations(playerId, location);
/*     */   }
/*     */   
/*     */   public static Location getLastDeathLocation(UUID playerId) {
/*  27 */     return lastDeathLocations.get(playerId);
/*     */   }
/*     */   
/*     */   public static void updateLastTeleportLocations(UUID playerId, Location location) {
/*  31 */     lastTeleportLocations.put(playerId, location);
/*  32 */     updateLastLocations(playerId, location);
/*     */   }
/*     */   
/*     */   public static Location getLastTeleportLocations(UUID playerId) {
/*  36 */     return lastTeleportLocations.get(playerId);
/*     */   }
/*     */   
/*     */   public static void updateLastLocations(UUID playerId, Location location) {
/*  40 */     lastLocations.put(playerId, location);
/*     */   }
/*     */   
/*     */   public static Location getLastLocation(UUID playerId) {
/*  44 */     return lastLocations.get(playerId);
/*     */   }
/*     */   
/*     */   public static boolean checkLastLocation(UUID playerId) {
/*  48 */     return lastLocations.containsKey(playerId);
/*     */   }
/*     */   
/*     */   public static void updateHomeLocation(UUID playerId, Location location) {
/*  52 */     homeLocations.put(playerId, location);
/*     */   }
/*     */   
/*     */   public static Location getHomeLocation(UUID playerId) {
/*  56 */     return homeLocations.get(playerId);
/*     */   }
/*     */   
/*     */   public static boolean checkHomeLocation(UUID playerId) {
/*  60 */     return homeLocations.containsKey(playerId);
/*     */   }
/*     */   
/*     */   public static void deleteHomeLocation(UUID playerId) {
/*  64 */     homeLocations.remove(playerId);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void updateWarpLocation(String warp, Location location) {
/*  69 */     warpLocations.put(warp, location);
/*     */   }
/*     */   
/*     */   public static Location getWarpLocation(String warp) {
/*  73 */     return warpLocations.get(warp);
/*     */   }
/*     */   
/*     */   public static HashMap<String, Location> getWarpHashMap() {
/*  77 */     return warpLocations;
/*     */   }
/*     */   
/*     */   public static boolean checkWarpLocation(String warp) {
/*  81 */     return warpLocations.containsKey(warp);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void deleteWarpLocation(String warp) {
/*  86 */     warpLocations.remove(warp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void loadFromConfig(FileConfiguration config) {
/*  92 */     ConfigurationSection lastDeathSection = config.getConfigurationSection("lastDeathLocations");
/*  93 */     if (lastDeathSection != null) {
/*  94 */       for (String key : ((ConfigurationSection)Objects.<ConfigurationSection>requireNonNull(config.getConfigurationSection("lastDeathLocations"))).getKeys(false)) {
/*  95 */         UUID playerId = UUID.fromString(key);
/*  96 */         Location location = (Location)config.get("lastDeathLocations." + key);
/*  97 */         lastDeathLocations.put(playerId, location);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 102 */     ConfigurationSection lastTeleportSection = config.getConfigurationSection("lastTeleportLocations");
/* 103 */     if (lastTeleportSection != null) {
/* 104 */       for (String key : ((ConfigurationSection)Objects.<ConfigurationSection>requireNonNull(config.getConfigurationSection("lastTeleportLocations"))).getKeys(false)) {
/* 105 */         UUID playerId = UUID.fromString(key);
/* 106 */         Location location = (Location)config.get("lastTeleportLocations." + key);
/* 107 */         lastTeleportLocations.put(playerId, location);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 112 */     ConfigurationSection lastSection = config.getConfigurationSection("lastLocations");
/* 113 */     if (lastSection != null) {
/* 114 */       for (String key : ((ConfigurationSection)Objects.<ConfigurationSection>requireNonNull(config.getConfigurationSection("lastLocations"))).getKeys(false)) {
/* 115 */         UUID playerId = UUID.fromString(key);
/* 116 */         Location location = (Location)config.get("lastLocations." + key);
/* 117 */         lastLocations.put(playerId, location);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 122 */     ConfigurationSection homeSection = config.getConfigurationSection("homeLocations");
/* 123 */     if (homeSection != null) {
/* 124 */       for (String key : ((ConfigurationSection)Objects.<ConfigurationSection>requireNonNull(config.getConfigurationSection("homeLocations"))).getKeys(false)) {
/* 125 */         UUID playerId = UUID.fromString(key);
/* 126 */         Location location = (Location)config.get("homeLocations." + key);
/* 127 */         homeLocations.put(playerId, location);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 132 */     ConfigurationSection warpSection = config.getConfigurationSection("warpLocations");
/* 133 */     if (warpSection != null) {
/* 134 */       for (String key : ((ConfigurationSection)Objects.<ConfigurationSection>requireNonNull(config.getConfigurationSection("warpLocations"))).getKeys(false)) {
/* 135 */         Location location = (Location)config.get("warpLocations." + key);
/* 136 */         warpLocations.put(key, location);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void saveToConfig(FileConfiguration config) {
/* 144 */     for (UUID playerId : lastDeathLocations.keySet()) {
/* 145 */       config.set("lastDeathLocations." + playerId, lastDeathLocations.get(playerId));
/*     */     }
/*     */ 
/*     */     
/* 149 */     for (UUID playerId : lastTeleportLocations.keySet()) {
/* 150 */       config.set("lastTeleportLocations." + playerId, lastTeleportLocations.get(playerId));
/*     */     }
/*     */ 
/*     */     
/* 154 */     for (UUID playerId : lastLocations.keySet()) {
/* 155 */       config.set("lastLocations." + playerId, lastLocations.get(playerId));
/*     */     }
/*     */ 
/*     */     
/* 159 */     for (UUID playerId : homeLocations.keySet()) {
/* 160 */       config.set("homeLocations." + playerId, homeLocations.get(playerId));
/*     */     }
/*     */ 
/*     */     
/* 164 */     for (String warpName : warpLocations.keySet())
/* 165 */       config.set("warpLocations." + warpName, warpLocations.get(warpName)); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cederik\Downloads\PitbullyPlugin-1.2.6.jar!\de\pitbully\pitbullyplugi\\utils\Locations.class
 * Java compiler version: 14 (58.0)
 * JD-Core Version:       1.1.3
 */