package com.test.dto;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.helper.SecurityHelper;

public class Block {
    final private String previousHash;

    final private String hash;

    final private int nonce;

    final private long timestamp;

    final ArrayList<Transaction> transactions;

    public Block(String previousHash, String hash, int nonce, long timestamp, ArrayList<Transaction> transactions) {
        this.previousHash = previousHash;
        this.hash = hash;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }

    public Block(String previousHash, int nonce, long timestamp, ArrayList<Transaction> transactions) {
        this.previousHash = previousHash;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.transactions = transactions;

        this.hash = this.computeHash();
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public String getHash() {
        return this.hash;
    }

    public int getNonce() {
        return this.nonce;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public String computeHash() {
        String data = this.previousHash + this.timestamp;

        for (Transaction trx: this.transactions) {
            data += trx.toString();
        }

        data += this.nonce;

        return SecurityHelper.hashSHA256(data);
    }

    public String toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("previousHash", this.previousHash);
        json.addProperty("hash", this.hash);
        json.addProperty("nonce", this.nonce);
        json.addProperty("timestamp", this.timestamp);

        JsonArray trxJsonArray = new JsonArray();
        for (Transaction trx: this.getTransactions()) {
            trxJsonArray.add(trx.toJson());
        }
        json.add("transactions", trxJsonArray);

        return new Gson().toJson(json);
    }
}
