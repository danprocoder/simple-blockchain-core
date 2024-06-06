package com.test.node;

import java.util.Map;

public class ConnectionHeader {
    private String firstLine;

    private Map<String, String> header;

    public ConnectionHeader(String firstLine, Map<String, String> header) {
        this.firstLine = firstLine;
        this.header = header;
    }

    public String getClientType() {
        if (this.firstLine.startsWith("MINER 1.0")) {
            return "miner";
        } else if (this.firstLine.startsWith("GET / HTTP/1.1")) {
            return "web-wallet";
        }

        return null;
    }

    public String getHeader(String key) {
        return this.header.get(key);
    }
}
