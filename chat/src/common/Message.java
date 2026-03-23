package com.chatapp.common;

public class Message {
    public String type;   // auth, chat, typing, file
    public String from;
    public String to;     // "login"/"register" for auth requests; username or "all" for chat
    public String body;
    public long ts;

    public Message() {}

    public Message(String type, String from, String to, String body, long ts) {
        this.type = type; this.from = from; this.to = to; this.body = body; this.ts = ts;
    }
}
