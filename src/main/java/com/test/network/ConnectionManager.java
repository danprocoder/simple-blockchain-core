package com.test.network;

import java.io.IOException;
import java.util.ArrayList;

import com.test.peer.MinerPeer;
import com.test.peer.NodePeer;
import com.test.peer.Peer;

public class ConnectionManager {
    private static ConnectionManager instance;
    
    /** Connected peers. */
    ArrayList<Peer> connectedPeers = new ArrayList<Peer>();

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    public void addPeer(Peer peer) {
        this.connectedPeers.add(peer);
    }

    public void broadcastToMiners(String message) {
        for (Peer connected: this.connectedPeers) {
            if (connected instanceof MinerPeer) {
                try {
                    connected.sendData(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void broadcastToNodes(String message) {
        for (Peer connected: this.connectedPeers) {
            if (connected instanceof NodePeer) {
                try {
                    connected.sendData(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
