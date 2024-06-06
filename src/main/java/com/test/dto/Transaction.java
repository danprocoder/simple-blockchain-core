package com.test.dto;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Transaction {
    private String from;

    private String to;

    private Double amount;

    private long timestamp;

    private String signature;

    public Transaction(String from, String to, Double amount, long timestamp, String signature) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public boolean verifySignature() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    
            byte[] publicKeyBytes = Base64.getDecoder().decode(this.from);
    
            PublicKey publicKey = keyFactory.generatePublic(
                new X509EncodedKeySpec(publicKeyBytes)
            );
    
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);

            String data = "Trx{from=" + this.from + ", to=" + this.to + ", amount=" + this.amount + ", timestamp=" + this.timestamp + "}";
            verifier.update(data.getBytes());

            return verifier.verify(this.signature.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
}
