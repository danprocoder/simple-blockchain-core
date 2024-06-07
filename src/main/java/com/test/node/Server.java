package com.test.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.test.controllers.Controller;
import com.test.controllers.AddBlockController;
import com.test.controllers.GetAddressBalanceController;
import com.test.controllers.GetBlockChainController;
import com.test.controllers.SendTransactionController;
import com.test.network.ConnectionHeader;
import com.test.network.ConnectionManager;
import com.test.network.peer.Peer;
import com.test.network.peer.PeerFactory;

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
                peer.setRoutes(this.getRoutes(peer));
                peer.initiateHandshake();

                peer.start();
                ConnectionManager.getInstance().addPeer(peer);
                
                System.out.println(header.getClientType() + " connected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HashMap<String, Controller> getRoutes(Peer peer) {
        HashMap<String, Controller> controllerMap = new HashMap<String, Controller>();

        controllerMap.put("add-block", new AddBlockController(peer));
        controllerMap.put("send-transaction", new SendTransactionController(peer));
        controllerMap.put("get-blockchain", new GetBlockChainController(peer));
        controllerMap.put("get-balance-for-address", new GetAddressBalanceController(peer));

        return controllerMap;
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
