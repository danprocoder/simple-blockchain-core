package com.test.network;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private String body;

    private final Map<String, String> header = new HashMap<String, String>();

    public Response(String channel) {
        header.put("Channel", channel);
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    public void addHeader(String key, int value) {
        this.header.put(key, Integer.toString(value));
    }

    public void setBody(String body) {
        this.body = body;
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
