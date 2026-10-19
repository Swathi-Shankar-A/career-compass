package com.career.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages JDBC connections for the Career Recommendation System.
 * Reads database configuration from db.properties and provides
 * connections using PreparedStatement-ready standard JDBC.
 */
public class DatabaseUtil {

    private static String url;
    private static String username;
    private static String password;
    private static String driverClassName;
    private static boolean initialized = false;

    static {
        init();
    }

    private static synchronized void init() {
        if (initialized) return;

        Properties props = new Properties();
        InputStream is = null;

        try {
            // 1. First attempt: Load from classpath
            is = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
            }

            // 2. Second attempt: Check local file system config/db.properties
            if (is == null) {
                File configFile = new File("config/db.properties");
                if (configFile.exists()) {
                    is = new FileInputStream(configFile);
                }
            }

            // 3. Third attempt: Check relative to working directory or WEB-INF
            if (is == null) {
                File fallback = new File("src/main/webapp/WEB-INF/classes/db.properties");
                if (fallback.exists()) {
                    is = new FileInputStream(fallback);
                }
            }

            if (is != null) {
                props.load(is);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                driverClassName = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                // Default fallback configuration for local standard setup
                url = "jdbc:mysql://localhost:3306/career_recommendation_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                username = "root";
                password = "root";
                driverClassName = "com.mysql.cj.jdbc.Driver";
            }

            Class.forName(driverClassName);
            initialized = true;
        } catch (Exception e) {
            System.err.println("[DatabaseUtil] Warning loading db.properties: " + e.getMessage());
            // Fallback
            url = "jdbc:mysql://localhost:3306/career_recommendation_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
            username = "root";
            password = "root";
            driverClassName = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(driverClassName);
            } catch (ClassNotFoundException ex) {
                System.err.println("[DatabaseUtil] MySQL Driver not found in classpath!");
            }
            initialized = true;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Obtains a new JDBC Connection to the MySQL database.
     * Callers should use try-with-resources to ensure proper closing.
     * 
     * @return Open java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            init();
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Checks if the database is reachable.
     * 
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Safely closes one or more JDBC resources without throwing exceptions.
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables == null) return;
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {}
            }
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }
}
