package com.test.controllers;

import java.util.ArrayList;

import com.google.gson.internal.LinkedTreeMap;
import com.test.blockchain.Blockchain;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Request;
import com.test.network.Response;
import com.test.network.peer.Peer;

public class AddBlockController extends Controller {
    public AddBlockController(Peer origin) {
        super(origin);
    }

    @Override()
    public Response onRequest(Request request) {
        LinkedTreeMap<String, Object> data = request.getData();

        ArrayList<LinkedTreeMap<String, Object>> maps = (ArrayList<LinkedTreeMap<String, Object>>) data.get("transactions");

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        try {
            for (LinkedTreeMap<String, Object> map: maps) {
                Transaction transaction = new Transaction(
                    (String) map.get("from"),
                    (String) map.get("to"),
                    ((Double) map.get("amount")).doubleValue(),
                    ((Double) map.get("timestamp")).longValue(),
                    (String) map.get("signature")
                );
                transactions.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Block block = new Block(
            (String) data.get("previousHash"),
            (String) data.get("hash"),
            ((Double) data.get("nonce")).intValue(),
            ((Double) data.get("timestamp")).longValue(),
            transactions
        );

        // Broadcast to other nodes on the network
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.broadcastToNodes(request.getRawJson());

        try {
            this.validateBlock(block);
            this.validateTransactions(transactions);
    
            this.addToLocalBlockchain(block);

            // Broadcast the verified block to wallets so they can alert the user.
            // TODO: refactor to only send to the wallet that sent the request.
            Response response = new Response("block-verified");
            response.setBody(block.toJson());
            connectionManager.broadcastToWebSocketClients(response);
        } catch (Exception e) {
            System.out.println("Illegal Block (" + block.getHash() + "): " + e.getMessage());
        }

        return null;
    }

    /**
     * Checks if the hash returned by the miner matches our own generated hash.
     *
     * @param block
     * @return
     * @throws Exception
     */
    private boolean validateBlock(Block block) throws Exception {
        if (!block.computeHash().equals(block.getHash())) {
            throw new Exception("Invalid Hash");
        }

        return true;
    }

    private boolean validateTransactions(ArrayList<Transaction> transactions) throws Exception {
        if (transactions.size() < 2) {
            throw new Exception("Block should have at least 2 transactions");
        }

        this.validateCoinbaseTransaction(transactions.get(0));

        for (int i = 1; i < transactions.size(); i++) {
            Transaction trx = transactions.get(i);

            if (!trx.verifySignature(trx.getFromAddress())) {
                throw new Exception("Illegal signature for transaction: " + trx.getHash());
            }
        }

        return true;
    }

    private boolean validateCoinbaseTransaction(Transaction coinbase) throws Exception {
        // TODO: Work out why this is not verifying
        // if (!coinbase.verifySignature(coinbase.getToAddress())) {
        //     throw new Exception("Coinbase: Verification failed with miners address");
        // }

        if (!coinbase.getFromAddress().isEmpty()) {
            throw new Exception("From address must be an empty string");
        }

        if (coinbase.getAmount() != 4) {
            throw new Exception("Miner overpaying");
        }

        return true;
    }

    private void addToLocalBlockchain(Block block) {
        Blockchain blockchain = Blockchain.getInstance();
        Block lastBlock = blockchain.getLastBlock();
        if (lastBlock == null || lastBlock.getHash().equals(block.getPreviousHash())) {
            blockchain.addBlock(block);
            System.out.println("Added to blockchain: " + block.getHash());
        } else {
            // Add to orphan block to be rearranged later.
            blockchain.addOrphanBlock(block);
        }

        // Update blockchain file
        blockchain.saveToFile();
    }
}
