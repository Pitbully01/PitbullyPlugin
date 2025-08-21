# PitbullyPlugin

[![Version](https://img.shields.io/badge/version-1.5.3-blue.svg)](https://github.com/Pitbully01/PitbullyPlugin/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.8-green.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![License](https://img.shields.io/badge/license-GPLv3-yellow.svg)](LICENSE)

A comprehensive Minecraft plugin providing teleportation commands including homes, warps, and back functionality for Paper/Spigot servers.

## ✨ Features

### 🏠 Home System
- **Set Home**: `/sethome` - Set your personal home location
- **Go Home**: `/home` - Teleport to your home location
- **Delete Home**: `/delhome` - Remove your home location

### 🌍 Warp System
- **Create Warps**: `/setwarp <name>` - Create server-wide warp points (Admin)
- **Use Warps**: `/warp <name>` - Teleport to any warp location
- **Delete Warps**: `/delwarp <name>` - Remove warp points (Admin)
- **Tab Completion**: Smart auto-completion for warp names

### ⬅️ Back Command
- **Go Back**: `/back` - Return to your last location (death or teleport)
- Automatically tracks death locations and teleportation history

### 🛡️ Admin Commands
- **Set Spawn**: `/setspawn` - Set the world spawn location
- **Keep XP**: Players with permission keep experience on death

### 🎒 Utility Commands
- **Enderchest**: `/enderchest` or `/ec` - Access your enderchest anywhere
- **Workbench**: `/workbench` or `/wb` - Open a crafting table anywhere

## 🏗️ Architecture

### Clean Storage Architecture (v1.5.1+)
- **Multiple Storage Backends**: Support for both file-based and database storage
- **Automatic Migration**: Seamless migration from file to database storage
- **Separation of Concerns**: Configuration (`config.yml`) separate from location data
- **Interface-Based Design**: `LocationStorage` interface with multiple implementations
- **Backward Compatibility**: Automatic migration from old single-file format
- **Extensible**: Easy to add new storage backends

### Database Support (v1.5.2+)
- **Multiple Databases**: MySQL, MariaDB, PostgreSQL, and SQLite support
- **Connection Pooling**: HikariCP for optimal database performance
- **Automatic Migration**: Seamless migration from `locations.yml` to database
- **Zero Downtime**: Switch storage types without losing data
- **Admin Choice**: Configure your preferred database in `config.yml`
- **Lightweight Plugin**: Database drivers installed separately for optimal performance

## 🚀 Installation

1. **Download** the latest release from [GitHub Releases](https://github.com/Pitbully01/PitbullyPlugin/releases)
2. **Place** the JAR file in your server's `plugins/` directory
3. **Database Setup** (optional): See [Database Setup Guide](DATABASE_SETUP.md) for database storage
4. **Restart** your server
5. **Configure** permissions as needed

## 📋 Requirements

- **Minecraft Version**: 1.21.3 (compatible with Paper/Spigot)
- **Java Version**: 21 or higher
- **Server Software**: Paper, Spigot, or compatible forks

## 🎮 Commands

| Command | Description | Permission | Aliases |
|---------|-------------|------------|---------|
| `/home` | Teleport to your home | `pitbullyplugin.home` | - |
| `/sethome` | Set your home location | `pitbullyplugin.sethome` | - |
| `/delhome` | Delete your home | `pitbullyplugin.delhome` | - |
| `/back` | Return to last location | `pitbullyplugin.back` | - |
| `/warp <name>` | Teleport to a warp | `pitbullyplugin.warp` | - |
| `/setwarp <name>` | Create a warp point | `pitbullyplugin.setwarp` | - |
| `/delwarp <name>` | Delete a warp point | `pitbullyplugin.delwarp` | - |
| `/enderchest [player]` | Open enderchest | `pitbullyplugin.enderchest` | `/ec` |
| `/workbench` | Open crafting table | `pitbullyplugin.workbench` | `/wb` |
| `/setspawn` | Set world spawn | `pitbullyplugin.setworldspawn` | - |

## 🔒 Permissions

### Basic Player Permissions
```yaml
pitbullyplugin.home: true          # Access to /home command
pitbullyplugin.sethome: true       # Access to /sethome command
pitbullyplugin.delhome: true       # Access to /delhome command
pitbullyplugin.back: true          # Access to /back command
pitbullyplugin.warp: true          # Access to /warp command
pitbullyplugin.enderchest: true    # Access to own enderchest
pitbullyplugin.workbench: true     # Access to /workbench command
```

### Admin Permissions
```yaml
pitbullyplugin.setwarp: true           # Create warp points
pitbullyplugin.delwarp: true           # Delete warp points
pitbullyplugin.setworldspawn: true     # Set world spawn
pitbullyplugin.enderchest.others: op   # Access other players' enderchests
```

### Special Permissions
```yaml
pitbullyplugin.keepxp: true        # Keep experience on death
```

## ⚙️ Configuration

### Storage Configuration

PitbullyPlugin supports both file-based and database storage for location data. You can choose your preferred storage method in the configuration.

#### File Storage (Default)
The traditional YAML-based storage system using `locations.yml`:
```yaml
database:
  storage-type: file  # Use file-based storage (default)
```

#### Database Storage
Store location data in a SQL database for better performance and scalability:
```yaml
database:
  storage-type: database  # Enable database storage
  
  connection:
    type: mysql  # mysql, mariadb, postgresql, or sqlite
    host: localhost
    port: 3306
    database: pitbully_plugin
    username: your_username
    password: your_password
    
    pool:
      max-connections: 10
      connection-timeout: 30000
      max-lifetime: 1800000
    
    ssl:
      enabled: false
      verify-server-certificate: true
```

#### Supported Databases
- **MySQL** - Recommended for production servers
- **MariaDB** - Excellent MySQL alternative
- **PostgreSQL** - Advanced database features
- **SQLite** - Single-file database, perfect for smaller servers

#### Automatic Migration
When switching from file to database storage:
1. Change `storage-type` from `file` to `database` in config.yml
2. Configure your database connection settings
3. Restart the server
4. All data from `locations.yml` is automatically migrated to the database
5. A backup of your original `locations.yml` is created in `migration-backups/`

### Plugin Configuration Files

The plugin uses two separate configuration files for better organization:

#### `config.yml` - Plugin Settings
```yaml
# Plugin Settings
settings:
  # Whether to automatically create backup files
  create-backups: true
  
  # Whether to show debug messages in console
  debug-mode: false
  
  # Teleportation safety settings
  teleport:
    # Check for safe teleport locations (avoid suffocation, lava, etc.)
    safety-check: true
    # Maximum distance to search for safe location
    max-safe-distance: 10

# Database Settings  
database:
  # Storage type: 'file' (default) or 'database'
  storage-type: file
  
  # Database connection settings (only used when storage-type is 'database')
  connection:
    type: mysql  # mysql, mariadb, postgresql, sqlite
    host: localhost
    port: 3306
    database: pitbully_plugin
    username: username
    password: password
    # ... additional connection settings
```

#### Configuration Management
- Edit `config.yml` directly to change plugin settings
- Restart the server or reload the plugin to apply changes
- All settings have safe defaults if not specified

#### `locations.yml` - Location Data (Auto-generated)
```yaml
# World spawn location (set via /setspawn command)
worldSpawnLocation: null

# Player home locations (set via /sethome command)  
homeLocations: {}

# Server warp locations (set via /setwarp command)
warpLocations: {}

# Player last death locations (automatically tracked for /back command)
lastDeathLocations: {}

# Player last teleport locations (automatically tracked for /back command)
lastTeleportLocations: {}

# Player last known locations (combination of death and teleport locations)
lastLocations: {}
```

**Note:** When database storage is enabled, this file is replaced by database tables.

#### Database Storage (Optional)
For high-performance servers, you can switch to database storage:

```yaml
database:
  storage-type: database        # Switch from 'file' to 'database'
  connection:
    type: mysql                 # mysql, mariadb, postgresql, sqlite
    host: localhost
    port: 3306
    database: pitbully_plugin
    username: your_username
    password: your_password
```

See [Database Setup Guide](DATABASE_SETUP.md) for detailed instructions.

#### Automatic Backup System
- Location data is automatically backed up (if enabled)
- Backups are stored in `/plugins/PitbullyPlugin/backups/`
- Backup files are timestamped: `locations_YYYY-MM-dd_HH-mm-ss.yml.bak`

### Automatic Migration
- **Existing Servers**: When upgrading from v1.5.0 or earlier, location data is automatically migrated from `config.yml` to `locations.yml`
- **Database Migration**: When switching to database storage, data is automatically migrated from `locations.yml` to database
- **Clean Separation**: After migration, `config.yml` is cleaned and ready for actual plugin configuration
- **Zero Downtime**: Migration happens seamlessly during plugin startup
- **Backup Creation**: Original files are automatically backed up before migration

## 🛡️ Safety Features

- **Safe Teleportation**: Automatically finds safe locations to prevent suffocation
- **Modern World Support**: Compatible with Minecraft 1.18+ world heights (-64 to 319)
- **Permission Checks**: All commands properly check permissions
- **Data Persistence**: All locations are automatically saved and loaded
- **Error Handling**: Comprehensive error handling with user-friendly messages

## 🔧 Development

### Building from Source

1. **Clone** the repository:
   ```bash
   git clone https://github.com/Pitbully01/PitbullyPlugin.git
   cd PitbullyPlugin
   ```

2. **Build** with Maven:
   ```bash
   mvn clean package
   ```

3. **Find** the compiled JAR in `target/PitbullyPlugin-1.5.3.jar`

### Development Requirements
- Java 21 JDK
- Maven 3.6+
- Paper API 1.21.3

## 📝 Changelog

### Version 1.5.1 (Latest)
- 🏗️ **Architecture**: Complete refactoring to clean, interface-based architecture
- 📁 **New**: Separate `locations.yml` file for location data storage
- 🔄 **Migration**: Automatic migration from old `config.yml` format
- 📚 **Documentation**: Comprehensive JavaDoc documentation for all storage classes
- 🔧 **Technical**: `LocationStorage` interface with `FileLocationStorage` implementation
- 🔧 **Technical**: `LocationManager` as static wrapper for backward compatibility
- ✅ **Compatibility**: 100% backward compatible - existing servers work without changes
- 🧹 **Clean**: Separation of configuration and data for better organization
- ⚙️ **New**: Configurable plugin settings (backups, debug mode, safety checks)
- 💾 **New**: Automatic backup system for location data
- 🛡️ **New**: Configurable teleportation safety features
- 🐛 **New**: Debug mode for detailed logging and troubleshooting

### Version 1.5.0
- ✨ **New**: Added `/setspawn` command for world spawn management
- ✨ **New**: Comprehensive JavaDoc documentation for all classes
- 🔄 **Breaking**: Updated permission system from `pit.*` to `pitbullyplugin.*`
- 🔄 **Breaking**: Complete plugin.yml restructure with proper descriptions
- 🚀 **Improved**: Refactored all 11 commands with consistent error handling
- 🚀 **Improved**: Enhanced SafeTeleport with modern Minecraft 1.18+ support
- 🚀 **Improved**: Better LocationListener with improved teleport tracking
- 🛠️ **Technical**: Professional code standards and comprehensive error handling
- 🛠️ **Technical**: Enhanced configuration management and data persistence

[View full changelog](CHANGELOG.md)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Guidelines
- Follow existing code style and conventions
- Add JavaDoc documentation for new methods and classes
- Test your changes thoroughly
- Update documentation as needed

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🐛 Bug Reports & Feature Requests

- **Bug Reports**: [GitHub Issues](https://github.com/Pitbully01/PitbullyPlugin/issues)
- **Feature Requests**: [GitHub Discussions](https://github.com/Pitbully01/PitbullyPlugin/discussions)

## 👨‍💻 Author

**Pitbully01**
- GitHub: [@Pitbully01](https://github.com/Pitbully01)
- Website: [nalumina.de](https://nalumina.de)

## 🙏 Acknowledgments

- Paper/Spigot team for the excellent server software
- Minecraft community for inspiration and feedback
- Contributors and users of this plugin

---

⭐ **If you find this plugin useful, please consider giving it a star on GitHub!**
