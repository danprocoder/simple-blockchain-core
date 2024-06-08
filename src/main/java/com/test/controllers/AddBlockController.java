package com.test.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import com.test.blockchain.Blockchain;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Message;
import com.test.network.RequestException;


/**
 * Handles request from miners to add verify a block and add it to our copy of the blockchain.
 */
public class AddBlockController extends Controller {

    @Override()
    public Message onRequest(Message request) throws RequestException {
        // Broadcast to other nodes on the network so that it can be verified by other node too.
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.broadcastToNodes(request);

        // Get the block from the request and verify it.
        HashMap<String, Object> data = request.getJsonBody();

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        Block block = new Block(
            (String) data.get("previousHash"),
            (String) data.get("hash"),
            ((Double) data.get("nonce")).intValue(),
            ((Double) data.get("timestamp")).longValue(),
            transactions
        );
        
        try {
            ArrayList<HashMap<String, Object>> maps = (ArrayList<HashMap<String, Object>>) data.get("transactions");

            for (HashMap<String, Object> map: maps) {
                Transaction transaction = new Transaction(
                    (String) map.get("from"),
                    (String) map.get("to"),
                    (Double) map.get("amount"),
                    ((Double) map.get("timestamp")).longValue(),
                    (String) map.get("signature")
                );
                transactions.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();

            throw new RequestException(e.getMessage(), request);
        }

        try {
            this.validateBlock(block);
            this.validateTransactions(transactions);
    
            this.addToLocalBlockchain(block);

            // Once the block is being verified. We sent it to all wallets connected on the network.
            Message response = new Message("block-verified");
            response.setBody(block.toJson());
            connectionManager.broadcastToWallets(response);
        } catch (Exception e) {
            System.out.println("Illegal Block (" + block.getHash() + "): " + e.getMessage());

            throw new RequestException(e.getMessage(), request);
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

    /**
     * Validates the transactions from the block.
     *
     * @param transactions
     * @return
     * @throws Exception
     */
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

            // TODO: check that the sender has enough money to cover the transaction.
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
