package com.test.controllers;

import java.util.ArrayList;

import com.google.gson.internal.LinkedTreeMap;
import com.test.blockchain.Blockchain;
import com.test.dto.Transaction;
import com.test.network.Request;
import com.test.network.Response;
import com.test.network.peer.Peer;

public class GetAddressBalanceController extends Controller {
    public GetAddressBalanceController(Peer peer) {
        super(peer);
    }

    @Override()
    public Response onRequest(Request request) {
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

        Response response = new Response("get-balance-for-address", "Number");
        response.addHeader("Address", address);
        response.setBody(Double.toString(balance));
        return response;
    }
}
