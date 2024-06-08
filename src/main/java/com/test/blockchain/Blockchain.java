package com.test.blockchain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.test.core.Coin;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.network.RequestException;

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

    /**
     * Load blockchain record from file on machine. If the file does not exists, we create one
     * with the genesis block.
     */
    public void initialize() {
        if (!instance.readFromFile()) {
            try {
                Block genesis = instance.getGenesisBlock();
                instance.chain.add(genesis);

                instance.saveToFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            for (Transaction trx: block.getTransactions()) {
                if (trx.getFromAddress().equals(address) || trx.getToAddress().equals(address)) {
                    transactionList.add(trx);
                }
            }
        }

        return transactionList;
    }

    public Transaction findTransactionByHash(String hash) {
        for (Block blk: this.chain) {
            for (Transaction t: blk.getTransactions()) {
                if (t.getHash().equals(hash)) {
                    return t;
                }
            }
        }

        return null;
    }

    public double getBalanceForAddress(String address) throws Exception {
        double balance = 0;

        ArrayList<Transaction> transactions = this.getTransactionsForAddress(address);
        if (transactions.isEmpty()) {
            throw new Exception("No transaction found for address");
        }

        for (Transaction trx: transactions) {
            if (trx.getFromAddress().equals(address)) {
                balance -= trx.getAmount();
            } else if (trx.getToAddress().equals(address)) {
                balance += trx.getAmount();
            }
        }

        return balance;
    }

    public double getTotalInCirculation() {
        double total = 0;

        return total;
    }

    /**
     * Creates the genesis block for the coin.
     *
     * @return
     * @throws Exception
     */
    private Block getGenesisBlock() throws Exception {
        ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

        Transaction trx = new Transaction(
            "",
            Coin.GENESIS_WALLET_ADDRESS,
            5000,
            1717779330000L,
            ""
        );
        transactionList.add(trx);

        Block genesis = new Block("", 1, 1234565454, transactionList);

        return genesis;
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

    private boolean readFromFile() {
        try {
            byte[] bytes = Files.readAllBytes(this.getFilePath());
            String content = new String(bytes);

            Gson gson = new Gson();
            List<LinkedTreeMap<String, Object>> list = gson.fromJson(content, ArrayList.class);
            for (LinkedTreeMap<String, Object> item: list) {
                ArrayList<Transaction> transactions = new ArrayList<Transaction>();

                Block blk = new Block(
                    (String) item.get("previousHash"),
                    (String) item.get("hash"),
                    ((Double) item.get("nonce")).intValue(),
                    ((Double) item.get("timestamp")).longValue(),
                    transactions
                );

                List<LinkedTreeMap<String, Object>> trxList = (List<LinkedTreeMap<String, Object>>) item.get("transactions");
                for (LinkedTreeMap<String, Object> trxObject: trxList) {
                    Transaction trx = new Transaction(
                        (String) trxObject.get("from"),
                        (String) trxObject.get("to"),
                        (Double) trxObject.get("amount"),
                        ((Double) trxObject.get("timestamp")).longValue(),
                        (String) trxObject.get("signature")
                    );

                    transactions.add(trx);
                }

                this.chain.add(blk);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveToFile() {
        try {
            JsonArray array = this.toJsonArray(true);
            Files.write(this.getFilePath(), new Gson().toJson(array).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getFilePath() {
        String userHome = System.getProperty("user.home");

        return Paths.get(userHome, ".coin", "blk.json");
    }
}
