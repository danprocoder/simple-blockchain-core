package com.test.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {
    /**
     * The message header.
     */
    private final Map<String, String> header = new HashMap<String, String>();

    /**
     * The sender of the message
     */
    private Peer origin;

    /**
     * The actual body of the message to send.
     */
    private String body;

    public Message(String event) {
        header.put("event", event);
        header.put("message-id", UUID.randomUUID().toString());
        header.put("content-type", "application/json");
    }

    public Message(String event, String contentType) {
        header.put("event", event);
        header.put("content-type", contentType);
    }

    public static Message fromText(String string) {
        return new Message("test-event");
    }

    public void setMessageId(String messageId) {
        this.header.put("message-id", messageId);
    }

    public void addHeader(String key, String value) {
        this.header.put(key.toLowerCase(), value);
    }

    public void addHeader(String key, int value) {
        this.header.put(key.toLowerCase(), Integer.toString(value));
    }

    public void setOrigin(Peer peer) {
        this.origin = peer;
    }

    public Peer getOrigin() {
        return this.origin;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return this.body;
    }
    
    @Override()
    public String toString() {
        String value = "";
        for (String key: this.header.keySet()) {
            value += "\r\n" + key + ": " + this.header.get(key);
        }

        if (this.body != null) {
            value += "\r\n\r\n" + this.body;
        }

        return value.trim();
    }
}
