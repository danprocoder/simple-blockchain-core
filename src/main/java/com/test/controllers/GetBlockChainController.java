package com.test.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.test.blockchain.Blockchain;
import com.test.network.Request;
import com.test.peer.Peer;

public class GetBlockChainController extends Controller {
    public GetBlockChainController(Peer origin) {
        super(origin);
    }
    
    @Override()
    public void onRequest(Request request) {
        try {
            JsonArray blockchain = Blockchain.getInstance().toJsonArray();
            String json = new Gson().toJson(blockchain);
            this.origin.sendData(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
