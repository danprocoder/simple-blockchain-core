package com.test.controllers;

import java.io.IOException;

import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Request;
import com.test.network.Response;
import com.test.peer.Peer;

public class SendTransactionController extends Controller {
    public SendTransactionController(Peer origin) {
        super(origin);
    }

    @Override()
    public void onRequest(Request request) {
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
                this.origin.sendData(new Response(422, "Failed to verify signature"));
                return;
            }

            this.origin.sendData(new Response(200, "Trx sent for mining"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionManager.getInstance().broadcastToMiners(request.getRawJson());
    }
}
