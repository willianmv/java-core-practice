package com.example.management.infrastructure.persistence.db.connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final String PROPERTIES_FILE = "db.properties";

    private static final String url;
    private static final String username;
    private static final String password;


    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {

            if(input == null) throw new RuntimeException("db.properties not found in classpath");

            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading db configuration");
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found", e);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting db connection");
        }
    }

}
