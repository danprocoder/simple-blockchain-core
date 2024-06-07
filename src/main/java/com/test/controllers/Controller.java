package com.test.controllers;

import com.test.network.Request;
import com.test.network.Response;
import com.test.network.peer.Peer;

public abstract class Controller {
    /**
     * The peer (miner, wallet or node) that sent this request.
     */
    protected Peer origin;

    /**
     * 
     * @param origin The peer (miner, wallet or node) that sent this request.
     */
    public Controller(Peer origin) {
        this.origin = origin;
    }

    public abstract Response onRequest(Request request);
}
