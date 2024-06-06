package com.test.peer;

import java.io.IOException;
import java.net.Socket;

import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

public class MinerPeer extends Peer {
    public MinerPeer(Socket socket, ConnectionHeader header, ServerListener listener) throws IOException {
        super(socket, header, listener);
    }
}
