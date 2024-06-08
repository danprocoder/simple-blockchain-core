package com.test.controllers;

import java.util.ArrayList;

import com.google.gson.internal.LinkedTreeMap;
import com.test.blockchain.Blockchain;
import com.test.dto.Transaction;
import com.test.network.Message;
import com.test.network.Request;

public class GetAddressBalanceController extends Controller {
    @Override()
    public Message onRequest(Request request) {
        LinkedTreeMap<String, Object> data = request.getData();

        String address = (String) data.get("address");

        Blockchain blockchain = Blockchain.getInstance();

        double balance = 0;
        ArrayList<Transaction> transactions = blockchain.getTransactionsForAddress(address);
        for (Transaction trx: transactions) {
            if (trx.getFromAddress().equals(address)) {
                balance -= trx.getAmount();
            } else if (trx.getToAddress().equals(address)) {
                balance += trx.getAmount();
            }
        }

        Message response = new Message("get-balance-for-address", "Number");
        response.setMessageId("same-as-request");
        response.addHeader("Address", address);
        response.setBody(Double.toString(balance));
        return response;
    }
}
