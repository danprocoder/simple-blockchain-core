package com.test.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.test.blockchain.Blockchain;
import com.test.network.Request;
import com.test.network.Response;
import com.test.peer.Peer;

public class GetBlockChainController extends Controller {
    public GetBlockChainController(Peer origin) {
        super(origin);
    }
    
    @Override()
    public void onRequest(Request request) {
        try {
            JsonArray blockchain = Blockchain.getInstance().toJsonArray(false);
            String json = new Gson().toJson(blockchain);

            Response response = new Response("blockchain-fetched");
            response.setBody(json);
            this.origin.sendData(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
