package com.test.controllers;

import java.util.HashMap;

import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Message;
import com.test.network.RequestException;

public class SendTransactionController extends Controller {
    @Override()
    public Message onRequest(Message request) throws RequestException {
        HashMap<String, Object> data = request.getJsonBody();

        Transaction trx;
        try {
            trx = new Transaction(
                (String) data.get("from"),
                (String) data.get("to"),
                (Double) data.get("amount"),
                ((Double) data.get("timestamp")).longValue(),
                (String) data.get("signature")
            );
        } catch (Exception e) {
            e.printStackTrace();

            throw new RequestException(e.getMessage(), request);
        }

        if (!trx.verifySignature(trx.getFromAddress())) {
            throw new RequestException("Invalid transaction signature", request);
        }

        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.broadcastToNodes(request);
        connectionManager.broadcastToMiners(request);

        // Send confirmation to wallet.
        Message response = new Message("send-transaction", "text/plain");
        response.setId(request.getId());
        response.setBody("Transaction send to miner");
        return response;
    }
}
