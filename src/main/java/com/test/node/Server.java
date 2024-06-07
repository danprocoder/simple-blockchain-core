package com.test.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.test.network.ConnectionHeader;
import com.test.network.ConnectionManager;
import com.test.peer.Peer;
import com.test.peer.PeerFactory;

public class Server {
    private static Server instance;

    private ServerSocket socketServer;


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

            try {
                ConnectionHeader header = this.getHeaders(client);
                Peer peer = PeerFactory.getPeer(client, header);
                peer.initiateHandshake();

                peer.start();
                ConnectionManager.getInstance().addPeer(peer);
                
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
}
