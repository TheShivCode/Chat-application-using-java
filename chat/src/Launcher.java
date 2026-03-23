package com.chatapp;

public class Launcher {
    public static void main(String[] args) {
        new Thread(() -> {
            try { com.chatapp.server.ServerMain.main(null); } catch (Exception e) { e.printStackTrace(); }
        }, "ServerThread").start();

        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        try { com.chatapp.client.ClientMain.main(null); } catch (Exception e) { e.printStackTrace(); }
    }
}
