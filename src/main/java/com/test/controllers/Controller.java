package com.test.controllers;

import com.test.network.Request;
import com.test.peer.Peer;

public abstract class Controller {
    protected Peer peer;

    public Controller(Peer peer) {
        this.peer = peer;
    }

    public abstract void onRequest(Request request);
}
