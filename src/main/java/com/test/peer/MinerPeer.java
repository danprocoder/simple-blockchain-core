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
    public void run() {
        this.fetchMessages();
    }

    @Override()
    protected void fetchMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = this.socket.getInputStream().read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);

                Gson gson = new Gson();
                Payload payload = gson.fromJson(message, Payload.class);
                LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) payload.data;
                if (payload.action.equals("block")) {
                    this.listener.onBlockReceived(
                        new Block(
                            (String) data.get("previousHash"),
                            (String) data.get("hash"),
                            (Double) data.get("nonce"),
                            (Double) data.get("timestamp")
                        ),
                        this,
                        message
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
