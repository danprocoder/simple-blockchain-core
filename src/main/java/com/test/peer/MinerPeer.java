package com.test.peer;

import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

public class MinerPeer extends Peer {
    public MinerPeer(Socket socket, ConnectionHeader header, ServerListener listener) throws IOException {
        super(socket, header, listener);
    }

    @Override()
    protected void onMessageFetched(byte[] bytes, int length) {
        String json = new String(bytes, 0, length);
        Payload payload = new Gson().fromJson(json, Payload.class);
        this.handleRequest(payload, json);
    }

    public String getAddress() {
        return this.header.getHeader("Address");
    }
}
