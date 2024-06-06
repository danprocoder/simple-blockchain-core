package com.test.peer;

import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Block;
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
        LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) payload.data;

        if (payload.action.equals("block")) {
            Block block = new Block(
                (String) data.get("previousHash"),
                (String) data.get("hash"),
                (Double) data.get("nonce"),
                (Double) data.get("timestamp")
            );
            this.listener.onBlockReceived(block, this, json);
        }
    }
}
