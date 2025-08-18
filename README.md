# PitbullyPlugin

[![Version](https://img.shields.io/badge/version-1.5.0-blue.svg)](https://github.com/Pitbully01/PitbullyPlugin/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.8-green.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

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

## 🚀 Installation

1. **Download** the latest release from [GitHub Releases](https://github.com/Pitbully01/PitbullyPlugin/releases)
2. **Place** the JAR file in your server's `plugins/` directory
3. **Restart** your server
4. **Configure** permissions as needed

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

The plugin automatically generates a `config.yml` file:

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

3. **Find** the compiled JAR in `target/PitbullyPlugin-1.5.0.jar`

### Development Requirements
- Java 21 JDK
- Maven 3.6+
- Paper API 1.21.3

## 📝 Changelog

### Version 1.5.0 (Latest)
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
