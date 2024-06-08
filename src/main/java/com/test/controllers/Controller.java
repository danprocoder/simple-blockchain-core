package com.test.controllers;

import com.test.network.Message;
import com.test.network.RequestException;

public abstract class Controller {
    // TODO: refactor to throw error exception to send an error back to the client.
    public abstract Message onRequest(Message request) throws RequestException;
}
