package com.chatapp.server;

import com.chatapp.common.Message;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final Gson gson = new Gson();

    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public ClientHandler(Socket socket) { this.socket = socket; }

    public synchronized void send(String json) throws IOException {
        if (out != null) { out.writeUTF(json); out.flush(); }
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String json = in.readUTF();
                Message msg = gson.fromJson(json, Message.class);
                if (msg == null || msg.type == null) continue;

                switch (msg.type) {
                    case "auth" -> {
                        // msg.to == "register" or "login"
                        if ("register".equalsIgnoreCase(msg.to)) {
                            boolean ok = AuthService.register(msg.from, msg.body);
                            Message resp = new Message("auth", "server", msg.from, ok ? "REGISTER_OK" : "REGISTER_FAIL", System.currentTimeMillis());
                            send(gson.toJson(resp));
                            if (ok) { this.username = msg.from; ServerMain.clients.put(username, this); System.out.println("User registered: " + username); }
                        } else { // login
                            boolean ok = AuthService.login(msg.from, msg.body);
                            Message resp = new Message("auth", "server", msg.from, ok ? "LOGIN_OK" : "LOGIN_FAIL", System.currentTimeMillis());
                            send(gson.toJson(resp));
                            if (ok) { this.username = msg.from; ServerMain.clients.put(username, this); System.out.println("User logged in: " + username); }
                        }
                    }
                    case "chat" -> {
                        if ("all".equals(msg.to)) {
                            for (ClientHandler h : ServerMain.clients.values()) {
                                if (h != this) { try { h.send(json); } catch (IOException ignored) {} }
                            }
                        } else {
                            ClientHandler target = ServerMain.clients.get(msg.to);
                            if (target != null) target.send(json);
                        }
                        Storage.saveMessage(msg);
                    }
                    case "typing" -> {
                        if ("all".equals(msg.to)) {
                            for (ClientHandler h : ServerMain.clients.values()) {
                                if (h != this) { try { h.send(json); } catch (IOException ignored) {} }
                            }
                        } else {
                            ClientHandler t = ServerMain.clients.get(msg.to);
                            if (t != null) t.send(json);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected: " + username);
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            if (username != null) ServerMain.clients.remove(username);
        }
    }
}
