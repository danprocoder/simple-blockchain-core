package com.test.peer;

import java.io.IOException;
import java.net.Socket;

import com.test.network.ConnectionHeader;

public class MinerPeer extends Peer {
    public MinerPeer(Socket socket, ConnectionHeader header) throws IOException {
        super(socket, header);
    }

    @Override()
    public void initiateHandshake() {
        // TODO Auto-generated method stub
    }

    public String getAddress() {
        return this.header.getHeader("Address");
    }
}
