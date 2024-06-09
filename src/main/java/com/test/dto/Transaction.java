package com.test.dto;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.test.helper.SecurityHelper;

public class Transaction {
    private String from;

    private String to;

    private double amount;

    private long timestamp;

    private String signature;

    private String hash;

    public Transaction(String from, String to, double amount, long timestamp, String signature) throws Exception {
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

    public double getAmount() {
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
            verifier.update(this.toString().getBytes());

            return verifier.verify(Base64.getDecoder().decode(this.signature));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String computeHash() throws Exception {
        return SecurityHelper.hashSHA256(this.toString());
    }

    public String toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("from", this.from);
        json.addProperty("to", this.to);
        json.addProperty("amount", this.amount);
        json.addProperty("timestamp", this.timestamp);
        json.addProperty("signature", this.signature);
        json.addProperty("hash", this.hash);

        return new Gson().toJson(json);
    }

    @Override()
    public String toString() {
        // Need to format the amount as converting double to string will not remove the trailing zeros causing the signature verification to fail
        // in some cases.
        DecimalFormat formatter = new DecimalFormat("0.#");
        return "Trx{from=" + this.from + ", to=" + this.to + ", amount=" + formatter.format(this.amount) + ", timestamp=" + this.timestamp + "}";
    }
}
