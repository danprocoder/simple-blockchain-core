package com.test.controllers;

import java.util.HashMap;

import com.test.blockchain.Blockchain;
import com.test.dto.Transaction;
import com.test.network.ConnectionManager;
import com.test.network.Message;
import com.test.network.RequestException;

/**
 * Handles requests from wallets to transfer token to another address.
 */
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

        // Verify that the user has enough money to carry out the transaction
        Blockchain blockchain = Blockchain.getInstance();

        double walletBalance;
        try {
            walletBalance = blockchain.getBalanceForAddress(trx.getFromAddress());
        } catch (Exception e) {
            throw new RequestException(e.getMessage(), request);
        }

        if (walletBalance < trx.getAmount()) {
            throw new RequestException("Not enough funds to carry out transaction", request);
        }

        // Broadcast to other nodes to further broadcast to their miners and
        // also broadcast to miners connected to this node.
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.broadcastToNodes(request);
        connectionManager.broadcastToMiners(request);

        // Send confirmation to wallet.
        Message response = new Message(request.getHeader("event"), "text/plain");
        response.setId(request.getId());
        response.setBody("Transaction send to miner");
        return response;
    }
}
