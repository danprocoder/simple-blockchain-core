package com.test.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ConnectionContext {
    private final String firstLine;

    private final Map<String, String> headerMap;

    private final String source;

    public ConnectionContext(Socket socket) throws IOException {
        // Handshake to workout if it's a wallet or miner.
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.firstLine = reader.readLine();

        this.headerMap = new HashMap<String, String>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(":");

            this.headerMap.put(header[0].trim().toLowerCase(), header[1].trim());
        }

        if (this.firstLine.startsWith("GET / HTTP/1.1")) {
            this.source = "websocket";
        } else {
            this.source = "tcp";
        }
    }

    /**
     * Returns a string representing if the client is a miner, a wallet or another node.
     *
     * @return
     */
    public String getClientType() {
        if (this.firstLine.startsWith("MINER 1.0")) {
            return "miner";
        } else if (this.firstLine.startsWith("NODE 1.0")) {
            return "node";
        } else if (this.firstLine.startsWith("GET / HTTP/1.1")) {
            // TODO: For now need refactoring as miners can also be wallets.
            return "wallet";
        }

        return null;
    }

    /**
     * Returns a string describing if the user connected via tcp or websocket.
     *
     * @return
     */
    public String getSource() {
        return this.source;
    }

    public String getHeader(String key) {
        return this.headerMap.get(key.toLowerCase());
    }
}
