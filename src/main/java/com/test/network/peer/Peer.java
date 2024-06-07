package com.test.network.peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.test.controllers.Controller;
import com.test.network.ConnectionHeader;
import com.test.network.Request;
import com.test.network.Response;

public abstract class Peer extends Thread {
    protected final Socket socket;
    protected final ConnectionHeader header;

    protected BufferedReader reader;
    protected DataOutputStream dos;

    HashMap<String, Controller> controllerMap;

    public Peer(Socket socket, ConnectionHeader header) throws IOException {
        this.socket = socket;
        this.header = header;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override()
    public void run() {
        this.fetchMessages();
    }

    public abstract void initiateHandshake() throws Exception;

    public void setRoutes(HashMap<String, Controller> routes) {
        this.controllerMap = routes;
    }

    protected void fetchMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = this.socket.getInputStream().read(buffer)) != -1) {
                String json = this.processBytesRead(buffer, bytesRead);

                Request request = new Gson().fromJson(json, Request.class);
                request.setRawJson(json);
                this.handleRequest(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String processBytesRead(byte[] buffer, int length) {
        return new String(Arrays.copyOfRange(buffer, 0, length));
    }

    public void sendData(byte[] b) throws IOException {
        this.dos.write(b);
        this.dos.flush();
    }

    public void sendData(Response response) throws IOException {
        this.sendData(response.toString().getBytes());
    }

    protected void handleRequest(Request request) {
        String eventName = request.getAction();

        Controller controller = this.controllerMap.get(eventName);
        if (controller == null) {
            try {
                Response response = new Response(eventName, "text/plain");
                response.setBody("Unknown event: " + eventName);
                this.sendData(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        Response response = controller.onRequest(request);
        if (response != null) {
            try {
                this.sendData(response);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
