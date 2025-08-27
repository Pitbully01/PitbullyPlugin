# MockBukkit Integration Testing

## Übersicht

Das PitbullyPlugin verwendet **MockBukkit** für Integrationstests, die eine echte Bukkit-Server-Umgebung simulieren. Dies ermöglicht es uns, echte `Location`-Objekte, Welten und Plugin-Funktionalität zu testen, ohne einen vollständigen Minecraft-Server zu starten.

## Warum MockBukkit?

### Das Problem ohne MockBukkit
Früher hatten wir Tests, die mit diesem Fehler fehlschlugen:
```
java.lang.NullPointerException: Cannot invoke "org.bukkit.Server.getWorld(String)" because "org.bukkit.Bukkit.server" is null
```

### Die Lösung mit MockBukkit
MockBukkit löst diese Probleme:
- ✅ **Echte Bukkit-Server-Umgebung**: Vollständiger Server-Mock mit Welten
- ✅ **Location-Serialisierung**: Echte `org.bukkit.Location` Objekte funktionieren
- ✅ **Plugin-Loading**: Das komplette Plugin kann geladen und getestet werden
- ✅ **Produktionsdaten**: Echte YAML-Dateien mit Bukkit-Serialisierung
- ✅ **CI/CD-fähig**: Läuft in GitHub Actions ohne Docker

## Test-Struktur

### RealServerMigrationTest.java
Diese Klasse demonstriert MockBukkit-Integration:

```java
@BeforeEach
void setUp() {
    // Start MockBukkit server
    server = MockBukkit.mock();
    MockBukkit.load(PitbullyPlugin.class);
    
    // Create test world - ermöglicht Location-Deserialisierung
    testWorld = server.addSimpleWorld("world");
}

@Test
void testRealProductionData() {
    // Lädt echte v1.6.0 Produktionsdaten
    FileLocationStorage fileStorage = new FileLocationStorage(tempDataFolder, Logger.getLogger("Test"));
    
    // Verifiziert dass echte Locations funktionieren
    Location warp = fileStorage.getWarpLocation("cave_entrance");
    assertThat(warp.getWorld()).isEqualTo(testWorld);
}
```

### Echte Produktionsdaten
Die Datei `real-bukkit-locations.yml` enthält:
- **Anonymisierte UUIDs** von echten Spielern
- **Echte Koordinaten** von Produktions-Server
- **Bukkit Location-Format** mit `==: org.bukkit.Location`
- **keepXp-Einstellungen** zur Migration-Tests

## GitHub Actions Integration

Die `.github/workflows/mockbukkit-tests.yml` führt die Tests automatisch aus:

```yaml
- name: Run MockBukkit Integration Tests
  run: |
    echo "🚀 Running MockBukkit Integration Tests..."
    mvn clean test -Dtest=RealServerMigrationTest
```

## Lokales Testen

```bash
# Nur MockBukkit Tests
mvn test -Dtest=RealServerMigrationTest

# Alle Tests (inklusive MockBukkit)
mvn clean test
```

## Dependencies

```xml
<!-- MockBukkit für echte Bukkit-Tests -->
<dependency>
    <groupId>com.github.seeseemelk</groupId>
    <artifactId>MockBukkit-v1.21</artifactId>
    <version>3.133.1</version>
    <scope>test</scope>
</dependency>
```

## Vorteile für das Projekt

1. **Produktionsreife Tests**: Echte Daten werden getestet
2. **Migration-Verifikation**: v1.6.0 → v1.6.1 Migration validiert
3. **CI/CD Ready**: Läuft automatisch in GitHub Actions
4. **Keine NPEs mehr**: Bukkit-Server ist immer verfügbar
5. **Realistische Tests**: Echte Location-Objekte, keine Mocks

## Entwicklung

Beim Hinzufügen neuer Features:
1. Erstelle Tests mit MockBukkit für Plugin-Funktionalität
2. Verwende echte Location-Objekte statt Mocks
3. Teste mit Produktionsdaten wenn möglich
4. Verifiziere in GitHub Actions

MockBukkit macht Bukkit-Plugin-Testing **endlich richtig**! 🎉
