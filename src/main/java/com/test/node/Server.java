package com.test.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.test.blockchain.Blockchain;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.peer.MinerPeer;
import com.test.peer.NodePeer;
import com.test.peer.Peer;
import com.test.peer.PeerFactory;

public class Server implements ServerListener {
    private static Server instance;

    private ServerSocket socketServer;

    /** Connected peers. */
    ArrayList<Peer> connectedPeers = new ArrayList<Peer>();

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }

        return instance;
    }

    public void listenForConnections(int port) throws IOException {
        this.socketServer = new ServerSocket(port);
        System.out.println("Node started on port " + port);

        while (true) {
            Socket client = this.socketServer.accept();

            // Handshake to workout if it's a wallet or miner.
            ConnectionHeader header = this.getHeaders(client);

            try {
                Peer peer = PeerFactory.getPeer(client, header, instance);
                peer.start();
                this.connectedPeers.add(peer);
                
                System.out.println(header.getClientType() + " connected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ConnectionHeader getHeaders(Socket client) throws IOException {
        // Handshake to workout if it's a wallet or miner.
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String firstLine = reader.readLine();

        Map<String, String> headerMap = new HashMap<String, String>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(":");

            headerMap.put(header[0].trim(), header[1].trim());
        }

        return new ConnectionHeader(firstLine, headerMap);
    }

    @Override()
    public void onTransactionReceived(Transaction trx, Peer peer, String rawMessage) {
        System.out.println("Transaction request received from wallet");
        try {
            if (!trx.verifySignature()) {
                System.out.println("Transaction verification failed. Sending message back");
                peer.sendData("422 Failed to verify signature");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Verified successfully. Broadcasting to miners.");
        for (Peer connected: this.connectedPeers) {
            if (!(connected instanceof MinerPeer)) {
                continue;
            }

            try {
                connected.sendData(rawMessage);
            } catch (IOException e) {
                System.out.println("Failed to send transaction to miner.");
            }
        }
    }

    @Override()
    public void onBlockReceived(Block block, Peer peer, String rawMessage) {
        if (!block.computeHash().equals(block.getHash())) {
            return;
        }

        Blockchain blockchain = Blockchain.getInstance();
        Block lastBlock = blockchain.getLastBlock();
        if (lastBlock == null || lastBlock.getHash().equals(block.getPreviousHash())) {
            blockchain.addBlock(block);
        } else {
            // Add to orphan block to be rearranged later.
            blockchain.addOrphanBlock(block);
        }

        // Broadcast to other nodes on the network
        for (Peer connected: this.connectedPeers) {
            try {
                if (!(connected instanceof NodePeer)) {
                    continue;    
                }

                connected.sendData(rawMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
