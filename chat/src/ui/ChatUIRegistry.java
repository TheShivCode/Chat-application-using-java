package com.chatapp.ui;

import com.chatapp.client.ClientConnection;

public class ChatUIRegistry {

    private static ChatUI active;
    private static ClientConnection connection;
    private static LoginCallback loginCallback;

    public interface LoginCallback {
        void onLoginSuccess(String username, ClientConnection conn);
    }

    public static void register(ChatUI u) {
        active = u;
    }

    public static ChatUI getActive() {
        return active;
    }

    public static void setConnection(ClientConnection c) {
        connection = c;
    }

    public static ClientConnection getConnection() {
        return connection;
    }

    public static void setLoginCallback(LoginCallback cb) {
        loginCallback = cb;
    }

    public static void onLoginSuccess(String username) {
        if (loginCallback != null && connection != null) {
            loginCallback.onLoginSuccess(username, connection);
        }
    }
}
