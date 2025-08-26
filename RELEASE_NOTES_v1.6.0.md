# Release Notes - PitbullyPlugin v1.6.0

Veröffentlicht am: 2025-08-26

## Highlights
- Neues TPA-System: `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny` mit konfigurierbarem Timeout
- Stabilere Konfiguration: Es werden nur noch fehlende Keys in `config.yml` ergänzt
- Aufräumen: Sämtliche Backup-Funktionalität wurde entfernt (Code & Doku)
- Besseres Lifecycle-Handling: Offene TPA-Anfragen werden beim Logout automatisch bereinigt
- Rechte & UX: Eigener Permission-Node für `/tpahere`, Korrekturen in `plugin.yml`
- Tests: Umfangreiche Unit-Tests für TPA-Logik und Befehle

## Neu
- Befehle
  - `/tpa <Spieler>`: Teleport-Anfrage an einen Spieler senden
  - `/tpahere <Spieler>`: Spieler bitten, sich zu dir zu teleportieren
  - `/tpaccept`: Eingehende Teleport-Anfrage annehmen
  - `/tpdeny`: Eingehende Teleport-Anfrage ablehnen
- Konfiguration
  - `settings.tpa.request-timeout-seconds` (Standard: 30) für Ablaufzeit von TPA-Anfragen

## Geändert
- Konfigurations-Manager: Schreibt nur fehlende Defaults; keine unnötigen Überschreibungen
- `TpaCommand`/`TpaHereCommand`: Robustere Prüfungen (Self-Check via `equalsIgnoreCase`, Offline-Checks)
- `plugin.yml`: Permission für `/tpahere` ergänzt und Formatierungsfehler korrigiert

## Entfernt
- Backup-Funktionalität vollständig gestrichen (inkl. Konfig-Optionen und Dateibackups)
- Migrations-Backups im Code entfernt

## Fehlerbehebungen
- Falsche Einrückung der `tpahere`-Permission in `plugin.yml` behoben
- Kleinere Verbesserungen bei Benutzerhinweisen und Fehlermeldungen

## Kompatibilität
- Getestet mit Paper 1.21.3 (API `1.21`)
- Benötigt Java 21+

## Upgrade-Hinweise
1. Aktualisiere die Berechtigungen (falls du ein Permission-Plugin nutzt):
   - `pitbullyplugin.tpa`, `pitbullyplugin.tpahere`, `pitbullyplugin.tpaccept`, `pitbullyplugin.tpdeny`
2. Optional: Setze den gewünschten Timeout in `config.yml` unter `settings.tpa.request-timeout-seconds`
3. Die frühere Backup-Funktion existiert nicht mehr. Eigene Backup-Strategie (z. B. Server-Backups) wird empfohlen.
4. Es ist keine Datenmigration erforderlich. File- und Datenbank-Storage funktionieren unverändert.

## Build/Download
- Artefakt: `target/PitbullyPlugin-1.6.0.jar` (sowie ggf. `-shaded`)

## Vollständiger Changelog
- Änderungen zwischen 1.5.4 und 1.6.0: https://github.com/Pitbully01/PitbullyPlugin/compare/1.5.4...1.6.0
