package com.test.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.test.blockchain.Blockchain;
import com.test.network.Request;
import com.test.network.Response;
import com.test.network.peer.Peer;

/**
 * Controller to handle requests to get all blockchain records.
 */
public class GetBlockChainController extends Controller {
    public GetBlockChainController(Peer origin) {
        super(origin);
    }
    
    @Override()
    public Response onRequest(Request request) {
        JsonArray blockchain = Blockchain.getInstance().toJsonArray(false);
        String json = new Gson().toJson(blockchain);

        Response response = new Response("get-blockchain");
        response.setBody(json);
        return response;
    }
}
