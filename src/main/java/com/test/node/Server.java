package com.test.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.test.controllers.AddBlockController;
import com.test.controllers.GetAddressBalanceController;
import com.test.controllers.GetAddressTransactions;
import com.test.controllers.GetBlockChainController;
import com.test.controllers.SendTransactionController;
import com.test.network.ConnectionManager;
import com.test.network.Peer;
import com.test.network.RequestHandler;

public class Server {
    private static Server instance;

    private ServerSocket socketServer;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
            instance.setupEventHandlers();
        }

        return instance;
    }

    /**
     * Starts the server and listens for connections.
     *
     * @param port
     * @throws IOException
     */
    public void listenForConnections(int port) throws IOException {
        // Start server
        this.socketServer = new ServerSocket(port);
        System.out.println("Node started on port " + port);
        System.out.println("Listening for connections...");

        while (true) {
            Socket client = this.socketServer.accept();

            try {
                Peer peer = new Peer(client);
                peer.initiateHandshake();

                peer.start();
                ConnectionManager.getInstance().addPeer(peer);
                
                System.out.println(peer.getContext().getClientType() + " connected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupEventHandlers() {
        // Setup router
        RequestHandler requestHandler = RequestHandler.getInstance();
        requestHandler.assign("add-block", new AddBlockController());
        requestHandler.assign("send-transaction", new SendTransactionController());
        requestHandler.assign("get-blockchain", new GetBlockChainController());
        requestHandler.assign("get-balance-for-address", new GetAddressBalanceController());
        requestHandler.assign("get-transactions-for-address", new GetAddressTransactions());
    }
}
