package com.test.network;

import com.google.gson.internal.LinkedTreeMap;

public class Request {
    private String action;

    private LinkedTreeMap<String, Object> data;

    private String rawJson;

    public Request(String action, Object data) {
        this.action = action;
        this.data = (LinkedTreeMap<String, Object>) data;
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
}
