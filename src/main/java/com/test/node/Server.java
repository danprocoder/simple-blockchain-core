package com.test.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.peer.MinerPeer;
import com.test.peer.Peer;
import com.test.peer.WebSocketPeer;

public class Server implements ServerListener {
    private static Server instance;

    private ServerSocket socketServer;

    /** Connected peers. */
    ArrayList<Peer> webSocketPeers = new ArrayList<Peer>();
    ArrayList<Peer> miners = new ArrayList<Peer>();

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }

        return instance;
    }

    public void start(int port) throws IOException {
        this.socketServer = new ServerSocket(port);
        System.out.println("Node started on port " + port);

        while (true) {
            Socket client = this.socketServer.accept();
            System.out.println("New peer connected (" + client.getRemoteSocketAddress() + ")");

            // Handshake to workout if it's a wallet or miner.
            ConnectionHeader header = this.getHeaders(client);

            if (header.getClientType().equals("web-wallet")) {
                Peer webWallet = new WebSocketPeer(client, header, this);
                webWallet.start();
                this.webSocketPeers.add(webWallet);
            } else if (header.getClientType().equals("miner")) {
                Peer peer = new MinerPeer(client, header, this);
                peer.start();
                this.miners.add(peer);
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
    public void onTransactionReceived(Transaction trx, Peer peer) {
        // try {
        //     System.out.println("verifying trx");
        //     if (!trx.verifySignature()) {
        //         System.out.println("Transaction verification failed. Sending message back");
        //         peer.sendData("422 Failed to verify signature");
        //         return;
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     return;
        // }

        for (Peer miner: this.miners) {
            try {
                miner.sendData(trx.toJson());
            } catch (IOException e) {
                System.out.println("Failed to send transaction to miner.");
            }
        }
    }

    @Override()
    public void onBlockReceived(Block block, Peer peer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
