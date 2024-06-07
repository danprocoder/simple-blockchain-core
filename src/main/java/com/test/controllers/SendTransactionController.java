package com.test.controllers;

import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Request;
import com.test.network.Response;
import com.test.network.peer.Peer;

public class SendTransactionController extends Controller {
    public SendTransactionController(Peer origin) {
        super(origin);
    }

    @Override()
    public Response onRequest(Request request) {
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
                Response response = new Response("send-transaction");
                response.addHeader("Status", 422);
                return response;
            }

            ConnectionManager.getInstance().broadcastToMiners(request.getRawJson());

            Response response = new Response("send-transaction", "text/plain");
            response.setBody("Transaction send to miner");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
