package com.test.network.peer;

import java.io.IOException;
import java.net.Socket;

import com.test.network.ConnectionHeader;

public class NodePeer extends Peer {
    public NodePeer(Socket client, ConnectionHeader header) throws IOException {
        super(client, header);
    }

    @Override()
    public void initiateHandshake() {
        // TODO Auto-generated method stub
    }
}
