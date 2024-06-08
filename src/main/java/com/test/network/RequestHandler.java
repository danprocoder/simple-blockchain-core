package com.test.network;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.test.controllers.Controller;

public class RequestHandler {
    private static RequestHandler instance;

    private final HashMap<String, Controller> handlerMap = new HashMap<String, Controller>();

    public static RequestHandler getInstance() {
        if (instance == null) {
            instance = new RequestHandler();
        }

        return instance;
    }

    public void assign(String eventName, Controller controller) {
        this.handlerMap.put(eventName, controller);
    }

    /**
     * Resolves the string message send to this Node by another connected node, miner or wallet.
     * Calls the right controller to handle the request.
     *
     * @param origin
     * @param message
     */
    public void resolve(Peer origin, String message) {
        // Parse data.
        // TODO: use Message.fromText() instead.
        // TODO: validate that message id is present.
        Request request = new Gson().fromJson(message, Request.class);
        request.setOrigin(origin);
        request.setRawJson(message);
        
        String eventName = request.getAction();

        try {
            Controller controller = this.handlerMap.get(eventName);
            if (controller == null) {
                Message response = new Message(eventName, "text/plain");
                // TODO: set message id to the id of the request.
                response.setBody("Unknown event: " + eventName);
                origin.sendData(response);

                return;
            }
    
            Message response = controller.onRequest(request);
            if (response != null) {
                origin.sendData(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
