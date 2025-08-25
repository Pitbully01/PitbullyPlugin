package de.pitbully.pitbullyplugin.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

    @Test
    @DisplayName("buildJdbcUrl should build proper JDBC URL for MySQL with SSL flags")
    void testBuildJdbcUrlMySql() {
        DatabaseConfig cfg = new DatabaseConfig(
                DatabaseConfig.DatabaseType.MYSQL,
                "localhost",
                3306,
                "pitbully",
                "user",
                "pass",
                10,
                30000,
                1800000,
                true,
                false
        );

        String url = cfg.buildJdbcUrl();
        assertThat(url)
            .startsWith("jdbc:mysql://localhost:3306/pitbully?useSSL=true")
            .contains("verifyServerCertificate=false")
            .contains("useUnicode=true")
            .contains("serverTimezone=UTC");
    }

    @Test
    @DisplayName("buildJdbcUrl should build SQLite file URL")
    void testBuildJdbcUrlSqlite() {
        DatabaseConfig cfg = new DatabaseConfig(
                DatabaseConfig.DatabaseType.SQLITE,
                "ignored",
                0,
                "testdb",
                "",
                "",
                1,
                0,
                0,
                false,
                true
        );

        String url = cfg.buildJdbcUrl();
        assertThat(url).isEqualTo("jdbc:sqlite:testdb.db");
    }
}
