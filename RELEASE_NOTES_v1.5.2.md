# Release Notes - PitbullyPlugin v1.5.2
**Release Date:** August 21, 2025  
**Download:** [GitHub Releases](https://github.com/Pitbully01/PitbullyPlugin/releases)

## 🎯 **TL;DR**
Added optional database storage support with automatic migration. Choose between file storage or MySQL/MariaDB/PostgreSQL/SQLite. Lightweight plugin with separate driver installation for optimal performance.

## ✨ **New Features**
- **🗃️ Database Storage:** Optional MySQL, MariaDB, PostgreSQL, and SQLite support
- **🔄 Auto-Migration:** Seamless migration from `locations.yml` to database
- **⚡ Connection Pooling:** HikariCP for optimal database performance
- **🛡️ Fallback System:** Automatic fallback to file storage if database fails
- **💾 Migration Backups:** Original data backed up before database migration
- **🔧 Flexible Config:** Easy database switching via `config.yml`
- **📦 Lightweight Plugin:** Smaller JAR with separate driver installation

## 📊 **Supported Databases**
- **MySQL** (recommended for production servers)
- **MariaDB** (excellent MySQL alternative)
- **PostgreSQL** (advanced database features)
- **SQLite** (single-file database for smaller servers)

## 📁 **Storage Options**
- **File Storage** (default) → `locations.yml` file
- **Database Storage** → Your choice of SQL database
- **Migration Backups** → `/migration-backups/` folder

## ⚙️ **Database Configuration**
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
    
    pool:
      max-connections: 10       # Connection pool size
      connection-timeout: 30000 # Timeout in milliseconds
      max-lifetime: 1800000     # Connection lifetime
```

## 🔄 **Migration Process**
1. Set `storage-type: database` in config.yml
2. Configure database connection details
3. Install JDBC driver (see [Database Setup Guide](DATABASE_SETUP.md))
4. Restart server
5. Plugin automatically migrates all data
6. Original `locations.yml` backed up
7. Ready for database operation!

## � **Driver Installation**
Database drivers are installed separately for optimal performance:
- **MySQL**: Download `mysql-connector-j-8.2.0.jar`
- **MariaDB**: Download `mariadb-java-client-3.3.3.jar`
- **PostgreSQL**: Download `postgresql-42.7.1.jar`
- **SQLite**: Download `sqlite-jdbc-3.45.0.0.jar`

Place in your server's `lib/` folder. See [Database Setup Guide](DATABASE_SETUP.md) for detailed instructions.

## �🛡️ **Compatibility**
- ✅ **100% Backward Compatible** - existing servers work unchanged
- ✅ **File Storage Default** - no configuration required for existing setups
- ✅ **Zero Downtime** - migration happens during server restart
- ✅ **Safe Fallback** - automatically switches to file storage if database fails
- ✅ **Lightweight Plugin** - smaller JAR file (~2MB instead of ~15MB)

## 🔧 **Technical Improvements**
- **Updated MySQL Connector** from deprecated `mysql-connector-java` to `mysql-connector-j`
- **Enhanced Shade Plugin** configuration to reduce build warnings
- **HikariCP Connection Pooling** for enterprise-grade performance
- **Prepared Statements** for SQL injection protection
- **Automatic Reconnection** handling for database connections
- **Connection Health Monitoring** with automatic failover
- **Optimized JAR Size** with provided scope dependencies

**Full Changelog**: https://github.com/Pitbully01/PitbullyPlugin/compare/1.5.1...1.5.2
