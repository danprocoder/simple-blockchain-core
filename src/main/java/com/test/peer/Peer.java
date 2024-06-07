package com.test.peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

public abstract class Peer extends Thread {
    protected final Socket socket;
    protected final ConnectionHeader header;
    protected final ServerListener listener;

    protected BufferedReader reader;
    protected DataOutputStream dos;

    public Peer(Socket socket, ConnectionHeader header, ServerListener listener) throws IOException {
        this.socket = socket;
        this.header = header;
        this.listener = listener;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override()
    public void run() {
        this.fetchMessages();
    }

    public void sendData(String payload) throws IOException {
        this.dos.write(payload.getBytes());
        this.dos.flush();
    }

    protected void fetchMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = this.socket.getInputStream().read(buffer)) != -1) {
                this.onMessageFetched(Arrays.copyOfRange(buffer, 0, bytesRead), bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void handleRequest(Payload payload, String rawJson) {
        LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) payload.data;
        switch (payload.action) {
            // Handles a request to add a block to a blockchain. Used after mining.
            case "block":
                Block block = new Block(
                    (String) data.get("previousHash"),
                    (String) data.get("hash"),
                    (Double) data.get("nonce"),
                    (Double) data.get("timestamp")
                );
                
                this.listener.onBlockReceived(block, this, rawJson);
                break;

            // Handles a request to make a transaction
            case "transaction":
                Transaction trx = new Transaction(
                    (String) data.get("from"),
                    (String) data.get("to"),
                    (Double) data.get("amount"),
                    ((Double) data.get("timestamp")).longValue(),
                    (String) data.get("signature")
                );
                this.listener.onTransactionReceived(trx, this, rawJson);
                break;

            // Handles a request to get a list of all the blocks in the current blockchain
            case "get-blockchain":
                try {
                    JsonArray blockchain = this.listener.onRequestBlockchain();
                    String json = new Gson().toJson(blockchain);
                    this.sendData(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    protected abstract void onMessageFetched(byte[] bytes, int length);
}
