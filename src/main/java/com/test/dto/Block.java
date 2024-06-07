package com.test.dto;

import java.util.ArrayList;

public class Block {
    private String previousHash;

    private String hash;

    private Double nonce;

    private Double timestamp;

    ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public Block(String previousHash, String hash, Double nonce, Double timestamp) {
        this.previousHash = previousHash;
        this.hash = hash;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public String getHash() {
        return this.hash;
    }

    public Double getNonce() {
        return this.nonce;
    }

    public Double getTimestamp() {
        return this.timestamp;
    }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public String computeHash() {
        return this.hash;
    }
}
