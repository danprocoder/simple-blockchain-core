package com.test.dto;

import java.util.ArrayList;

public class Block {
    final private String previousHash;

    final private String hash;

    final private Double nonce;

    final private Double timestamp;

    final ArrayList<Transaction> transactions;

    public Block(String previousHash, String hash, Double nonce, Double timestamp, ArrayList<Transaction> transactions) {
        this.previousHash = previousHash;
        this.hash = hash;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.transactions = transactions;
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
