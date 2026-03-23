package com.chatapp.server;

import com.chatapp.db.Database;
import com.chatapp.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public static boolean register(String username, String password) {
        String hash = PasswordUtils.sha256(password);
        try (Connection c = Database.getConnection();
             PreparedStatement p = c.prepareStatement("INSERT INTO users(username,password_hash) VALUES(?,?)")) {
            p.setString(1, username);
            p.setString(2, hash);
            p.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean login(String username, String password) {
        String hash = PasswordUtils.sha256(password);
        try (Connection c = Database.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT password_hash FROM users WHERE username = ?")) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return hash.equals(r.getString(1));
            }
        } catch (Exception e) { }
        return false;
    }
}
