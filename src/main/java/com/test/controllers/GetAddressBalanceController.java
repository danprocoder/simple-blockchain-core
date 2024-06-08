package com.test.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import com.test.blockchain.Blockchain;
import com.test.dto.Transaction;
import com.test.network.Message;

public class GetAddressBalanceController extends Controller {
    @Override()
    public Message onRequest(Message request) {
        HashMap<String, Object> data = request.getJsonBody();

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
        response.setId(request.getId());
        response.addHeader("Address", address);
        response.setBody(Double.toString(balance));
        return response;
    }
}
