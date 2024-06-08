package com.test.network;

import java.io.IOException;
import java.util.ArrayList;

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

    /**
     * Send a message to all miners connected on the server.
     *
     * @param message
     */
    public void broadcastToMiners(Message message) {
        for (Peer connected: this.connectedPeers) {
            if (connected.isMiner()) {
                try {
                    connected.sendData(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send a message to other node servers we are connected to.
     *
     * @param message
     */
    public void broadcastToNodes(Message message) {
        for (Peer connected: this.connectedPeers) {
            if (connected.isNode()) {
                try {
                    connected.sendData(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send a message all wallets connected on the server.
     *
     * @param message
     */
    public void broadcastToWallets(Message message) {
        for (Peer connected: this.connectedPeers) {
            if (connected.isWallet()) {
                try {
                    connected.sendData(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
