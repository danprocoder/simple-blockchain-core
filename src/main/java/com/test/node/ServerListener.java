package com.test.node;

import com.test.dto.Block;
import com.test.dto.Transaction;
import com.test.peer.Peer;

public interface ServerListener {
    public void onTransactionReceived(Transaction trx, Peer peer);

    public void onBlockReceived(Block block, Peer peer);
}
