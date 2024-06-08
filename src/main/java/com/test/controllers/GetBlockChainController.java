package com.test.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.test.blockchain.Blockchain;
import com.test.network.Message;

/**
 * Controller to handle requests to get all blockchain records.
 */
public class GetBlockChainController extends Controller {
    @Override()
    public Message onRequest(Message request) {
        JsonArray blockchain = Blockchain.getInstance().toJsonArray(false);
        String json = new Gson().toJson(blockchain);

        Message response = new Message("get-blockchain");
        response.setId(request.getId());
        response.setBody(json);
        return response;
    }
}
