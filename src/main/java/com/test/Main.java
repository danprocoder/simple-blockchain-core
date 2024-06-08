package com.test;

import java.io.IOException;

import com.test.blockchain.Blockchain;
import com.test.node.Server;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize the blockchain
            Blockchain blockchain = Blockchain.getInstance();
            blockchain.initialize();

            // Start the server and listen for connections
            Server server = Server.getInstance();
            server.listenForConnections(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
