package com.test.controllers;

import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Message;
import com.test.network.Request;

public class SendTransactionController extends Controller {
    @Override()
    public Message onRequest(Request request) {
        LinkedTreeMap<String, Object> data = request.getData();

        try {
            Transaction trx = new Transaction(
                (String) data.get("from"),
                (String) data.get("to"),
                (Double) data.get("amount"),
                ((Double) data.get("timestamp")).longValue(),
                (String) data.get("signature")
            );

            if (!trx.verifySignature(trx.getFromAddress())) {
                // TODO: maybe throw an exception from here instead.
                Message response = new Message("send-transaction");
                response.setMessageId("same-as-request");
                response.addHeader("Status", 422);
                return response;
            }

            // Broadcast the transaction to all connected nodes so that they can send to
            // miners connected to them. Also send to miners to start the mining process.
            // TODO: When request changes to message, we won't need to make a new message again.
            Message broadcast = new Message("send-transaction");
            broadcast.setBody(request.getRawJson());

            ConnectionManager connectionManager = ConnectionManager.getInstance();
            connectionManager.broadcastToNodes(broadcast);
            connectionManager.broadcastToMiners(request.getRawJson());

            // Send confirmation to wallet.
            Message response = new Message("send-transaction", "text/plain");
            response.setMessageId("same-as-request");
            response.setBody("Transaction send to miner");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
