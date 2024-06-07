package com.test.controllers;

import java.io.IOException;

import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Request;
import com.test.peer.Peer;

public class SendTransactionController extends Controller {
    public SendTransactionController(Peer peer) {
        super(peer);
    }

    @Override()
    public void onRequest(Request request) {
        LinkedTreeMap<String, Object> data = request.getData();

        Transaction trx = new Transaction(
            (String) data.get("from"),
            (String) data.get("to"),
            (Double) data.get("amount"),
            ((Double) data.get("timestamp")).longValue(),
            (String) data.get("signature")
        );

        try {
            if (!trx.verifySignature()) {
                peer.sendData("422 Failed to verify signature");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ConnectionManager.getInstance().broadcastToMiners(request.getRawJson());
    }
}
