package com.test.network;

import java.io.IOException;
import java.util.HashMap;

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
        try {
            try {
                Message msg = Message.fromText(message);
        
                if (msg.getId() == null) {
                    throw new RequestException("Message ID is required", msg);
                }
    
                String eventName = msg.getHeader("event");
                if (eventName == null) {
                    throw new RequestException("Event is required", msg);
                }
    
                Controller controller = this.handlerMap.get(eventName);
                if (controller == null) {
                    throw new RequestException("Unknown event: " + eventName, msg);
                }
    
                // Set the origin when the necessary validations have passed.
                msg.setOrigin(origin);
        
                Message response = controller.onRequest(msg);
                if (response != null) {
                    response.setId(msg.getId());
                    origin.sendData(response);
                }
            } catch (RequestException e) {
                origin.sendData(e.getMessage().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
