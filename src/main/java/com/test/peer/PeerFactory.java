package com.test.peer;

import java.net.Socket;

import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

public class PeerFactory {
    public static Peer getPeer(Socket client, ConnectionHeader header, ServerListener listener) throws Exception {
        switch (header.getClientType()) {
            case "web-wallet":
                return new WebSocketPeer(client, header, listener);
            case "miner":
                return new MinerPeer(client, header, listener);
            case "node":
                return new NodePeer(client, header, listener);
            default:
                throw new Exception("Unknown client type");
        }
    }
}
