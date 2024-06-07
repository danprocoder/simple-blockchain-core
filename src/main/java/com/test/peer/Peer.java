package com.test.peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.test.controllers.AddBlockController;
import com.test.controllers.Controller;
import com.test.controllers.GetAddressBalanceController;
import com.test.controllers.GetBlockChainController;
import com.test.controllers.SendTransactionController;
import com.test.network.ConnectionHeader;
import com.test.network.Request;
import com.test.network.Response;

public abstract class Peer extends Thread {
    protected final Socket socket;
    protected final ConnectionHeader header;

    protected BufferedReader reader;
    protected DataOutputStream dos;

    HashMap<String, Controller> controllerMap = new HashMap<String, Controller>();

    public Peer(Socket socket, ConnectionHeader header) throws IOException {
        this.socket = socket;
        this.header = header;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());

        // Assign actions to controllers here.
        // TODO: This might need to be refactored and moved somewhere.
        controllerMap.put("block", new AddBlockController(this));
        controllerMap.put("transaction", new SendTransactionController(this));
        controllerMap.put("get-blockchain", new GetBlockChainController(this));
        controllerMap.put("get-balance-for-address", new GetAddressBalanceController(this));
    }

    @Override()
    public void run() {
        this.fetchMessages();
    }

    public abstract void initiateHandshake() throws Exception;

    public void sendData(String payload) throws IOException {
        this.dos.write(payload.getBytes());
        this.dos.flush();
    }

    public void sendData(Response response) throws IOException {
        this.dos.write(response.toString().getBytes());
        this.dos.flush();
    }

    protected void fetchMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = this.socket.getInputStream().read(buffer)) != -1) {
                String json = this.processBytesRead(buffer, bytesRead);
                this.onMessageFetched(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String processBytesRead(byte[] buffer, int length) {
        return new String(Arrays.copyOfRange(buffer, 0, length));
    }

    protected void handleRequest(Request request) {
        Controller controller = this.controllerMap.get(request.getAction());
        controller.onRequest(request);
    }

    protected void onMessageFetched(String json) {
        Request request = new Gson().fromJson(json, Request.class);
        request.setRawJson(json);
        this.handleRequest(request);
    }
}
