package com.test.dto;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Formatter;

public class Transaction {
    private String from;

    private String to;

    private Double amount;

    private long timestamp;

    private String signature;

    private String hash;

    public Transaction(String from, String to, Double amount, long timestamp, String signature) throws Exception {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
        this.signature = signature;
        this.hash = this.computeHash();
    }

    public String getFromAddress() {
        return this.from;
    }

    public String getToAddress() {
        return this.to;
    }

    public Double getAmount() {
        return this.amount;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getHash() {
        return this.hash;
    }

    public String getSignature() {
        return this.signature;
    }

    public boolean verifySignature(String key) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    
            byte[] publicKeyBytes = Base64.getDecoder().decode(key);
    
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
    
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            verifier.update(this.getStringData().getBytes());

            return verifier.verify(Base64.getDecoder().decode(this.signature));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String computeHash() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(this.getStringData().getBytes());

        Formatter formatter = new Formatter();
        for (byte b: hashBytes) formatter.format("%02x", b);
        String hex = formatter.toString();

        formatter.close();
        
        return hex;
    }

    public String getStringData() {
        return "Trx{from=" + this.from + ", to=" + this.to + ", amount=" + this.amount + ", timestamp=" + this.timestamp + "}";
    }
}
