package com.test.peer;

import java.io.IOException;
import java.net.Socket;

import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

public class NodePeer extends Peer {
    public NodePeer(Socket client, ConnectionHeader header, ServerListener listener) throws IOException {
        super(client, header, listener);
    }

    @Override()
    public void onMessageFetched(Payload payload, String json) {

    }
}
