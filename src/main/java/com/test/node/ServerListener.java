package com.test.node;

import com.google.gson.JsonArray;
import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.peer.Peer;

public interface ServerListener {
    public void onTransactionReceived(Transaction trx, Peer peer, String rawMessage);

    public void onBlockReceived(Block block, Peer peer, String message);

    public JsonArray onRequestBlockchain();
}
