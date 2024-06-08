package com.test.controllers;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.test.blockchain.Blockchain;
import com.test.dto.Transaction;
import com.test.network.Message;
import com.test.network.RequestException;

public class GetAddressTransactions extends Controller {
    @Override()
    public Message onRequest(Message request) throws RequestException {
        Blockchain blockchain = Blockchain.getInstance();

        String address = request.getBody();

        ArrayList<Transaction> transactions = blockchain.getTransactionsForAddress(address);
        if (transactions.isEmpty()) {
            throw new RequestException("No transaction found for address", request);
        }

        JsonArray jsonArray = new JsonArray();
        for (Transaction trx: transactions) {
            jsonArray.add(trx.toJson());
        }

        Message res = new Message(request.getHeader("event"), "application/json");
        res.setId(request.getId());
        res.addHeader("Address", address);
        res.setBody(new Gson().toJson(jsonArray));

        return res;
    } 
}
