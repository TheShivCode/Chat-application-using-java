package com.chatapp.server;

import com.chatapp.common.Message;
import com.chatapp.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Storage {
    public static void saveMessage(Message m) {
        try (Connection c = Database.getConnection();
             PreparedStatement p = c.prepareStatement("INSERT INTO messages(sender,receiver,body,ts) VALUES(?,?,?,?)")) {
            p.setString(1, m.from);
            p.setString(2, m.to);
            p.setString(3, m.body);
            p.setLong(4, m.ts);
            p.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
