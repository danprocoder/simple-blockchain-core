package com.test.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.test.blockchain.Blockchain;
import com.test.network.Message;
import com.test.network.Request;

/**
 * Controller to handle requests to get all blockchain records.
 */
public class GetBlockChainController extends Controller {
    @Override()
    public Message onRequest(Request request) {
        JsonArray blockchain = Blockchain.getInstance().toJsonArray(false);
        String json = new Gson().toJson(blockchain);

        Message response = new Message("get-blockchain");
        response.setMessageId("same-as-request");
        response.setBody(json);
        return response;
    }
}
