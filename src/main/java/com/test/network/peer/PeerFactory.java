package com.test.network.peer;

import java.net.Socket;

import com.test.network.ConnectionHeader;

public class PeerFactory {
    public static Peer getPeer(Socket client, ConnectionHeader header) throws Exception {
        switch (header.getClientType()) {
            case "web-wallet":
                return new WebSocketPeer(client, header);
            case "miner":
                return new MinerPeer(client, header);
            case "node":
                return new NodePeer(client, header);
            default:
                throw new Exception("Unknown client type");
        }
    }
}
