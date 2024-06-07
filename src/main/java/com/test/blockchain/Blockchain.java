package com.test.blockchain;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.dto.Block;
import com.test.dto.Transaction;

public class Blockchain {
    ArrayList<Block> chain = new ArrayList<Block>();
    ArrayList<Block> orphanBlocks = new ArrayList<Block>();

    private static Blockchain instance;

    public static Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }

        return instance;
    }

    public void addBlock(Block block) {
        this.chain.add(block);
    }

    public void addOrphanBlock(Block block) {
        this.orphanBlocks.add(block);
    }

    public Block getLastBlock() {
        int blockLength = this.chain.size();
        if (blockLength > 0) {
            return this.chain.get(blockLength - 1);
        }

        return null;
    }

    /**
     * Returns all the transaction made to/from the provided address on the stored blockchain.
     *
     * @param address the address to check
     * @return ArrayList a list transactions
     */
    public ArrayList<Transaction> getTransactionsForAddress(String address) {
        ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

        for (Block block: this.chain) {
            for (Transaction transaction: block.getTransactions()) {
                if (transaction.getFromAddress().equals(address) || transaction.getToAddress().equals(address)) {
                    transactionList.add(transaction);
                }
            }
        }

        return transactionList;
    }

    public double getTotalInCirculation() {
        double total = 0;

        return total;
    }

    public JsonArray toJsonArray(boolean expand) {
        JsonArray blockchainJson = new JsonArray();

        for (Block block: this.chain) {
            JsonObject blockJson = new JsonObject();

            blockJson.addProperty("previousHash", block.getPreviousHash());
            blockJson.addProperty("hash", block.getHash());
            blockJson.addProperty("nonce", block.getNonce());
            blockJson.addProperty("timestamp", block.getTimestamp());

            JsonArray trxJsonArray = new JsonArray();
            for (Transaction trx: block.getTransactions()) {
                if (expand) {
                    JsonObject trxObject = new JsonObject();
                    trxObject.addProperty("from", trx.getFromAddress());
                    trxObject.addProperty("to", trx.getToAddress());
                    trxObject.addProperty("amount", trx.getAmount());
                    trxObject.addProperty("timestamp", trx.getTimestamp());
                    trxObject.addProperty("signature", trx.getSignature());
                    trxObject.addProperty("hash", trx.getHash());
    
                    trxJsonArray.add(trxObject);
                } else {
                    trxJsonArray.add(trx.getHash());
                }
            }
            blockJson.add("transactions", trxJsonArray);

            blockchainJson.add(blockJson);
        }

        return blockchainJson;
    }

    public void saveToFile() {

    }
}
