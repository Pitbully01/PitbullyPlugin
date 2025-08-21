package de.pitbully.pitbullyplugin.storage;

/**
 * Configuration class for database connection settings.
 * 
 * <p>This class encapsulates all the database connection parameters and settings
 * needed to establish a connection to various types of SQL databases.
 * 
 * @author Pitbully01
 * @since 1.5.2
 */
public class DatabaseConfig {
    
    /**
     * Enum representing supported database types.
     */
    public enum DatabaseType {
        MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://"),
        MARIADB("mariadb", "org.mariadb.jdbc.Driver", "jdbc:mariadb://"),
        POSTGRESQL("postgresql", "org.postgresql.Driver", "jdbc:postgresql://"),
        SQLITE("sqlite", "org.sqlite.JDBC", "jdbc:sqlite:");
        
        private final String name;
        private final String driverClass;
        private final String urlPrefix;
        
        DatabaseType(String name, String driverClass, String urlPrefix) {
            this.name = name;
            this.driverClass = driverClass;
            this.urlPrefix = urlPrefix;
        }
        
        public String getName() { return name; }
        public String getDriverClass() { return driverClass; }
        public String getUrlPrefix() { return urlPrefix; }
        
        public static DatabaseType fromString(String type) {
            for (DatabaseType dbType : values()) {
                if (dbType.name.equalsIgnoreCase(type)) {
                    return dbType;
                }
            }
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
    
    private final DatabaseType type;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxConnections;
    private final long connectionTimeout;
    private final long maxLifetime;
    private final boolean sslEnabled;
    private final boolean sslVerifyServerCertificate;
    
    public DatabaseConfig(DatabaseType type, String host, int port, String database, 
                         String username, String password, int maxConnections, 
                         long connectionTimeout, long maxLifetime, 
                         boolean sslEnabled, boolean sslVerifyServerCertificate) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;
        this.connectionTimeout = connectionTimeout;
        this.maxLifetime = maxLifetime;
        this.sslEnabled = sslEnabled;
        this.sslVerifyServerCertificate = sslVerifyServerCertificate;
    }
    
    /**
     * Constructs the JDBC URL for this database configuration.
     * 
     * @return The complete JDBC URL string
     */
    public String buildJdbcUrl() {
        StringBuilder url = new StringBuilder(type.getUrlPrefix());
        
        switch (type) {
            case SQLITE:
                // For SQLite, the "host" is actually the file path
                url.append(database).append(".db");
                break;
            default:
                // For network databases
                url.append(host).append(":").append(port).append("/").append(database);
                
                // Add SSL and other parameters
                url.append("?useSSL=").append(sslEnabled);
                if (sslEnabled) {
                    url.append("&verifyServerCertificate=").append(sslVerifyServerCertificate);
                }
                
                // Add additional parameters based on database type
                switch (type) {
                    case MYSQL:
                        url.append("&useUnicode=true&characterEncoding=UTF-8");
                        url.append("&autoReconnect=true&useJDBCCompliantTimezoneShift=true");
                        url.append("&useLegacyDatetimeCode=false&serverTimezone=UTC");
                        break;
                    case MARIADB:
                        url.append("&useUnicode=true&characterEncoding=UTF-8");
                        url.append("&autoReconnect=true");
                        break;
                    case POSTGRESQL:
                        url.append("&useUnicode=true&characterEncoding=UTF-8");
                        break;
                    case SQLITE:
                        // SQLite doesn't need additional URL parameters
                        break;
                }
                break;
        }
        
        return url.toString();
    }
    
    // Getters
    public DatabaseType getType() { return type; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getMaxConnections() { return maxConnections; }
    public long getConnectionTimeout() { return connectionTimeout; }
    public long getMaxLifetime() { return maxLifetime; }
    public boolean isSslEnabled() { return sslEnabled; }
    public boolean isSslVerifyServerCertificate() { return sslVerifyServerCertificate; }
    
    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "type=" + type +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", username='" + username + '\'' +
                ", password='[HIDDEN]'" +
                ", maxConnections=" + maxConnections +
                ", connectionTimeout=" + connectionTimeout +
                ", maxLifetime=" + maxLifetime +
                ", sslEnabled=" + sslEnabled +
                ", sslVerifyServerCertificate=" + sslVerifyServerCertificate +
                '}';
    }
}
