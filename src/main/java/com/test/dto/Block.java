package com.test.dto;

public class Block {
    private String previousHash;

    private String hash;

    private Double nonce;

    private Double timestamp;

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

    public String computeHash() {
        return this.hash;
    }
}
