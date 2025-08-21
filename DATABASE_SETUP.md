# Database Setup Guide - PitbullyPlugin v1.5.2

This guide explains how to set up database storage for PitbullyPlugin.

## 📁 **Storage Options**

### File Storage (Default)
- ✅ **No additional setup required**
- ✅ **Works out of the box**
- ✅ **Data stored in `locations.yml`**

### Database Storage (Optional)
- 🗃️ **Better performance for large servers**
- 🔄 **Supports multiple database types**
- ⚡ **Connection pooling for optimal performance**

## 🗃️ **Supported Databases**

### MySQL (Recommended)
```yaml
database:
  storage-type: database
  connection:
    type: mysql
    host: localhost
    port: 3306
    database: pitbully_plugin
    username: your_username
    password: your_password
```

### MariaDB
```yaml
database:
  storage-type: database
  connection:
    type: mariadb
    host: localhost
    port: 3306
    database: pitbully_plugin
    username: your_username
    password: your_password
```

### PostgreSQL
```yaml
database:
  storage-type: database
  connection:
    type: postgresql
    host: localhost
    port: 5432
    database: pitbully_plugin
    username: your_username
    password: your_password
```

### SQLite (Single File)
```yaml
database:
  storage-type: database
  connection:
    type: sqlite
    database: pitbully_plugin  # Creates pitbully_plugin.db file
```

## ⚙️ **Database Driver Installation**

### Plugin JAR Size Options

#### Option A: Lightweight JAR (Recommended)
The plugin JAR includes only HikariCP for connection pooling. Database drivers must be installed separately.

**Pros:**
- ✅ Smaller plugin file size
- ✅ No driver conflicts
- ✅ Server admin controls which drivers are available

**Cons:**
- ❌ Requires manual driver installation

#### Option B: All-in-One JAR
Include all database drivers in the plugin JAR (larger file size).

### Manual Driver Installation (Option A)

Download the appropriate JDBC driver and place it in your server's `lib` folder:

#### MySQL Driver
```bash
# Download mysql-connector-j-8.2.0.jar
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar
# Place in server/lib/ folder
```

#### MariaDB Driver
```bash
# Download mariadb-java-client-3.3.3.jar
wget https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.3.3/mariadb-java-client-3.3.3.jar
# Place in server/lib/ folder
```

#### PostgreSQL Driver
```bash
# Download postgresql-42.7.1.jar
wget https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar
# Place in server/lib/ folder
```

#### SQLite Driver
```bash
# Download sqlite-jdbc-3.45.0.0.jar
wget https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.0.0/sqlite-jdbc-3.45.0.0.jar
# Place in server/lib/ folder
```

## 🛠️ **Database Setup**

### MySQL/MariaDB Setup
```sql
-- Create database
CREATE DATABASE pitbully_plugin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'pitbully_user'@'localhost' IDENTIFIED BY 'secure_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON pitbully_plugin.* TO 'pitbully_user'@'localhost';
FLUSH PRIVILEGES;
```

### PostgreSQL Setup
```sql
-- Create database
CREATE DATABASE pitbully_plugin WITH ENCODING 'UTF8';

-- Create user
CREATE USER pitbully_user WITH PASSWORD 'secure_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE pitbully_plugin TO pitbully_user;
```

### SQLite Setup
No additional setup required - database file is created automatically.

## 🔄 **Migration Process**

1. **Configure Database**: Edit `config.yml` with your database settings
2. **Install Driver**: Place JDBC driver in `server/lib/` (if using lightweight JAR)
3. **Restart Server**: Plugin will automatically detect the change
4. **Automatic Migration**: All data from `locations.yml` is migrated
5. **Backup Created**: Original file is backed up in `migration-backups/`

## 🛡️ **Security Considerations**

- ✅ Use strong database passwords
- ✅ Create dedicated database users with minimal permissions
- ✅ Consider SSL connections for remote databases
- ✅ Regularly backup your database

## 🔧 **Connection Pool Settings**

```yaml
database:
  connection:
    pool:
      max-connections: 10        # Adjust based on server load
      connection-timeout: 30000  # 30 seconds
      max-lifetime: 1800000      # 30 minutes
    
    ssl:
      enabled: false             # Enable for remote databases
      verify-server-certificate: true
```

## 🐛 **Troubleshooting**

### Common Issues

#### "Driver not found" Error
- ✅ Ensure JDBC driver is in `server/lib/` folder
- ✅ Restart server after adding driver
- ✅ Check driver filename matches expected version

#### Connection Timeout
- ✅ Verify database is running
- ✅ Check host/port settings
- ✅ Verify firewall allows connections

#### Permission Denied
- ✅ Check database user permissions
- ✅ Verify database exists
- ✅ Test connection with database client

### Fallback to File Storage
If database connection fails, the plugin automatically falls back to file storage and logs the error.

## 📊 **Performance Comparison**

| Feature | File Storage | Database Storage |
|---------|--------------|------------------|
| Setup Complexity | ✅ Simple | ⚠️ Moderate |
| Performance (Small) | ✅ Excellent | ✅ Excellent |
| Performance (Large) | ⚠️ Good | ✅ Excellent |
| Backup/Restore | ✅ Simple | ⚠️ Moderate |
| Multi-Server | ❌ No | ✅ Yes |
| Query Capability | ❌ Limited | ✅ Full SQL |

**Recommendation:** Use file storage for small servers (<100 players), database storage for larger servers or multi-server setups.
