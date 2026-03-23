package com.chatapp.client;

public class ClientMain {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try { new com.chatapp.ui.LoginUI(); } catch (Exception e) { e.printStackTrace(); }
        });
    }
}
