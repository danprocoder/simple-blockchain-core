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

    public double getAddressBalance(String address) {
        double balance = 0;

        return balance;
    }

    public double getTotalInCirculation() {
        double total = 0;

        return total;
    }

    public JsonArray toJsonArray() {
        JsonArray array = new JsonArray();

        for (Block block: this.chain) {
            JsonObject object = new JsonObject();

            object.addProperty("previousHash", block.getPreviousHash());
            object.addProperty("hash", block.getHash());
            object.addProperty("nonce", block.getNonce());
            object.addProperty("timestamp", block.getTimestamp());

            JsonArray transactions = new JsonArray();
            for (Transaction trx: block.getTransactions()) {
                JsonObject trxObject = new JsonObject();
                trxObject.addProperty("from", trx.getFromAddress());
                trxObject.addProperty("to", trx.getToAddress());
                trxObject.addProperty("amount", trx.getAmount());
                trxObject.addProperty("timestamp", trx.getTimestamp());

                transactions.add(trxObject);
            }

            array.add(object);
        }

        return array;
    }

    public void saveToFile() {

    }
}
