package com.test.network;

import com.google.gson.internal.LinkedTreeMap;

@Deprecated()
public class Request {
    private String action;

    private LinkedTreeMap<String, Object> data;

    private String rawJson;

    private Peer peer;

    private Message message;

    public Request(String action, Object data) {
        this.action = action;
        this.data = (LinkedTreeMap<String, Object>) data;
    }

    public void setOrigin(Peer peer) {
        this.peer = peer;
    }

    public Peer getOrigin() {
        return this.peer;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    public String getAction() {
        return this.action;
    }

    public LinkedTreeMap<String, Object> getData() {
        return this.data;
    }

    public String getRawJson() {
        return this.rawJson;
    }

    public Message getMessage() {
        return this.message;
    }
}
