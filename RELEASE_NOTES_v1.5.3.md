# Release Notes - PitbullyPlugin v1.5.3
**Release Date:** August 21, 2025  
**Download:** [GitHub Releases](https://github.com/Pitbully01/PitbullyPlugin/releases)

## 🎯 **TL;DR**
Fixed critical data loss bug where configuration files were overwritten on server restart. Improved startup logging and implemented dynamic version system for better maintainability.

## 🔧 **Bug Fixes**
- **🚨 Data Loss Prevention:** Fixed bug where `locations.yml` was overwritten with empty data on server restart
- **📝 Config Generation:** Fixed empty `config.yml` creation - now properly populated with default values
- **⏰ Timing Issues:** Resolved initialization timing problems that caused storage errors during startup
- **💾 Auto-Save System:** Implemented automatic saving every 5 minutes to prevent data loss

## ✨ **Improvements**
- **🧹 Clean Startup Logs:** Reduced verbose startup messages, moved detailed logs to debug mode
- **⚙️ Debug Mode:** Added `debug-mode: true/false` setting for detailed logging when needed
- **📊 Dynamic Versioning:** Implemented build-time version system - no more hardcoded version strings
- **ℹ️ Plugin Info Command:** New `/pitbullyinfo` command for version and build information

## 🛠️ **Technical Changes**
- **Safe Initialization:** Improved plugin startup sequence to prevent data overwrites
- **Better Error Handling:** Enhanced fallback mechanisms when LocationManager isn't ready
- **Version Management:** All version info now comes from `pom.xml` via Maven resource filtering
- **Build Information:** Embedded build timestamp and Java version for better support

## 📊 **New Command**
```yaml
/pitbullyinfo  # Shows detailed plugin version and build information
             # Permission: pitbullyplugin.info (default: op)
```

## 🔍 **Debug Mode**
Enable detailed logging in `config.yml`:
```yaml
settings:
  debug-mode: true  # Shows detailed startup and operation logs
```

## 🛡️ **Compatibility**
- ✅ **100% Backward Compatible** - all existing data preserved
- ✅ **Safe Migration** - existing servers upgrade seamlessly
- ✅ **Data Protection** - multiple safeguards against data loss
- ✅ **Auto-Recovery** - automatic fallback mechanisms

**Full Changelog**: https://github.com/Pitbully01/PitbullyPlugin/compare/1.5.2...1.5.3
