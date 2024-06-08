package com.test.controllers;

import java.util.HashMap;

import com.test.blockchain.Blockchain;
import com.test.network.Message;
import com.test.network.RequestException;

public class GetAddressBalanceController extends Controller {
    @Override()
    public Message onRequest(Message request) throws RequestException {
        HashMap<String, Object> data = request.getJsonBody();

        String address = (String) data.get("address");

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
