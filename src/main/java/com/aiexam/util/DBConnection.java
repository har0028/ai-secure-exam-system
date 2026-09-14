package com.aiexam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Centralized JDBC connection provider.
 * Reads connection details from db.properties on the classpath.
 * Includes automatic fallback to an embedded H2 file database (in MySQL mode)
 * if MySQL connection fails or is unconfigured.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    private static boolean useH2Fallback = false;
    private static final String H2_URL = "jdbc:h2:file:./target/ai_exam_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (is != null) {
                props.load(is);
                URL = props.getProperty("db.url");
                USERNAME = props.getProperty("db.username");
                PASSWORD = props.getProperty("db.password");
                DRIVER = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed loading db.properties: " + e.getMessage());
        }

        // Allow Environment Variables to override properties for production security
        String envUrl = System.getenv("DB_URL");
        if (envUrl == null || envUrl.trim().isEmpty()) {
            envUrl = System.getenv("JDBC_DATABASE_URL");
        }
        if (envUrl == null || envUrl.trim().isEmpty()) {
            envUrl = System.getenv("MYSQL_URL");
        }
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            URL = envUrl;
        }

        String envUser = System.getenv("DB_USERNAME");
        if (envUser == null || envUser.trim().isEmpty()) {
            envUser = System.getenv("DB_USER");
        }
        if (envUser != null && !envUser.trim().isEmpty()) {
            USERNAME = envUser;
        }

        String envPass = System.getenv("DB_PASSWORD");
        if (envPass == null || envPass.trim().isEmpty()) {
            envPass = System.getenv("DB_PASS");
        }
        if (envPass != null && !envPass.trim().isEmpty()) {
            PASSWORD = envPass;
        }

        String envDriver = System.getenv("DB_DRIVER");
        if (envDriver != null && !envDriver.trim().isEmpty()) {
            DRIVER = envDriver;
        }

        // Test primary MySQL connection
        boolean mysqlSuccess = false;
        if (URL != null && DRIVER != null) {
            try {
                Class.forName(DRIVER);
                try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                    mysqlSuccess = true;
                    LOGGER.info("Successfully connected to MySQL database: " + URL);
                }
            } catch (Throwable t) {
                LOGGER.warning("Could not connect to MySQL (" + t.getMessage() + "). Switching to H2 Embedded DB fallback.");
            }
        }

        if (!mysqlSuccess) {
            useH2Fallback = true;
            try {
                Class.forName("org.h2.Driver");
                initH2Database();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize embedded H2 database: " + e.getMessage(), e);
            }
        }
    }

    private DBConnection() {
        // utility class
    }

    private static void initH2Database() {
        try (Connection conn = DriverManager.getConnection(H2_URL, H2_USER, H2_PASS);
             Statement stmt = conn.createStatement()) {

            // Check if tables already initialized
            boolean tablesExist = false;
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "USERS", null)) {
                if (rs.next()) {
                    tablesExist = true;
                }
            }

            if (!tablesExist) {
                LOGGER.info("Initializing H2 database schema and seed data...");
                InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("schema_h2.sql");
                if (is == null) {
                    is = DBConnection.class.getClassLoader().getResourceAsStream("schema.sql");
                }
                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("--") || line.isEmpty() || line.startsWith("DROP DATABASE") || line.startsWith("CREATE DATABASE") || line.startsWith("USE ")) {
                            continue;
                        }
                        // Remove ENGINE=InnoDB
                        line = line.replaceAll("(?i)ENGINE\\s*=\\s*InnoDB", "");
                        sb.append(line).append(" ");
                    }
                    String[] sqlStatements = sb.toString().split(";");
                    for (String sql : sqlStatements) {
                        sql = sql.trim();
                        if (!sql.isEmpty()) {
                            try {
                                stmt.execute(sql);
                            } catch (SQLException ex) {
                                LOGGER.warning("H2 init SQL execution warning: " + ex.getMessage() + " for SQL: " + sql);
                            }
                        }
                    }
                    LOGGER.info("H2 database successfully initialized.");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing H2 schema: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a fresh JDBC connection.
     */
    public static Connection getConnection() throws SQLException {
        if (useH2Fallback) {
            return DriverManager.getConnection(H2_URL, H2_USER, H2_PASS);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
