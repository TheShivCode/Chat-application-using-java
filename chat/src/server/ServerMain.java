package com.chatapp.server;

import com.chatapp.db.Database;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    public static final int PORT = 8080;
    public static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        Class.forName("com.chatapp.db.Database"); // ensure DB init
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            }
        }
    }
}
