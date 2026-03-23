package com.chatapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:chatapp.db";

    static {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS users(
                  username TEXT PRIMARY KEY,
                  password_hash TEXT
                );
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS messages(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  sender TEXT,
                  receiver TEXT,
                  body TEXT,
                  ts INTEGER
                );
            """);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
