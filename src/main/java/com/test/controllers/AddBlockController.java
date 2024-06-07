package com.test.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import com.test.blockchain.Blockchain;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Request;
import com.test.peer.MinerPeer;
import com.test.peer.Peer;

public class AddBlockController extends Controller {
    public AddBlockController(Peer origin) {
        super(origin);
    }

    @Override()
    public void onRequest(Request request) {
        LinkedTreeMap<String, Object> data = request.getData();

        ArrayList<LinkedTreeMap<String, Object>> maps = (ArrayList<LinkedTreeMap<String, Object>>) data.get("transactions");

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        for (LinkedTreeMap<String, Object> map: maps) {
            Transaction transaction = new Transaction(
                (String) map.get("from"),
                (String) map.get("to"),
                (Double) map.get("amount"),
                ((Double) map.get("timestamp")).longValue(),
                (String) map.get("signature")
            );
            transactions.add(transaction);
        }

        Block block = new Block(
            (String) data.get("previousHash"),
            (String) data.get("hash"),
            (Double) data.get("nonce"),
            (Double) data.get("timestamp"),
            transactions
        );
        // Broadcast to other nodes on the network
        ConnectionManager.getInstance().broadcastToNodes(request.getRawJson());

        if (!block.computeHash().equals(block.getHash())) {
            System.out.println("Block rejected (" + block.getHash() + "): Hash mismatch");
            return;
        }

        String minerAddress = ((MinerPeer) this.origin).getAddress();

        Blockchain blockchain = Blockchain.getInstance();
        Block lastBlock = blockchain.getLastBlock();
        if (lastBlock == null || lastBlock.getHash().equals(block.getPreviousHash())) {
            blockchain.addBlock(block);
            System.out.println("Added to blockchain: " + block.getHash());
        } else {
            // Add to orphan block to be rearranged later.
            blockchain.addOrphanBlock(block);
        }

        try {
            JsonArray array = Blockchain.getInstance().toJsonArray();
            Files.write(Paths.get("output.json"), new Gson().toJson(array).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
