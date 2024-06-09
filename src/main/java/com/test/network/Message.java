package com.test.network;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        header.put("message-id", UUID.randomUUID().toString());
        header.put("content-type", contentType);
    }

    public Message() {}

    public static Message fromText(String str) {
        String[] segments = str.split("\\r?\\n\\r?\\n");

        Message message = new Message();

        // Grab the headers and save them.
        String[] headers = segments[0].split("\\r?\\n");
        for (String h: headers) {
            String[] header = h.split(":");
            if (header.length < 2) {
                System.out.println(h);
                continue;
            }
            message.addHeader(header[0].trim().toLowerCase(), header[1].trim());
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < segments.length; i++) {
            sb.append(segments[i]);
            
            if (i < segments.length - 1) {
                sb.append("\r\n\r\n");
            }
        }
        message.setBody(sb.toString());

        return message;
    }

    public void setId(String messageId) {
        this.header.put("message-id", messageId);
    }

    public String getId() {
        return this.header.get("message-id");
    }

    public void addHeader(String key, String value) {
        this.header.put(key.toLowerCase(), value);
    }

    public void addHeader(String key, int value) {
        this.header.put(key.toLowerCase(), Integer.toString(value));
    }

    public String getHeader(String key) {
        return this.header.get(key);
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

    public HashMap<String, Object> getJsonBody() {
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        return new Gson().fromJson(this.body, type);
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
