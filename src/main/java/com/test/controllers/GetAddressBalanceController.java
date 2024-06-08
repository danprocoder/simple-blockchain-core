package com.test.controllers;

import com.test.blockchain.Blockchain;
import com.test.network.Message;
import com.test.network.RequestException;

public class GetAddressBalanceController extends Controller {
    @Override()
    public Message onRequest(Message request) throws RequestException {
        String address = request.getBody();

        Blockchain blockchain = Blockchain.getInstance();

        double balance;
        try {
            balance = blockchain.getBalanceForAddress(address);
        } catch (Exception e) {
            throw new RequestException(e.getMessage(), request);
        }

        Message response = new Message(request.getHeader("event"), "text/plain");
        response.setId(request.getId());
        response.addHeader("Address", address);
        response.setBody(Double.toString(balance));
        return response;
    }
}
