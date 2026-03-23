package com.chatapp.client;

import com.chatapp.common.Message;
import com.chatapp.ui.ChatUI;
import com.chatapp.ui.ChatUIRegistry;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final Gson gson = new Gson();

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        // register active connection for LoginUI → ChatUI
        ChatUIRegistry.setConnection(this);

        // listen server messages
        new Thread(() -> {
            try {
                while (true) {
                    String json = in.readUTF();
                    ChatUI.onServerMessage(json);
                }
            } catch (Exception e) {
                System.out.println("Connection lost.");
            }
        }, "ClientReadThread").start();
    }

    public synchronized void send(Message m) throws Exception {
        String json = gson.toJson(m);
        out.writeUTF(json);
        out.flush();
    }
}
