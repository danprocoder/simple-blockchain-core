package com.test.blockchain;

import java.util.ArrayList;

import com.test.dto.Block;

public class Blockchain {
    ArrayList<Block> chain = new ArrayList<Block>();

    ArrayList<Block> orphanBlocks = new ArrayList<Block>();

    private static Blockchain instance;

    public static Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }

        return instance;
    }

    public void addBlock(Block block) {
        this.chain.add(block);
    }

    public void addOrphanBlock(Block block) {
        this.orphanBlocks.add(block);
    }

    public Block getLastBlock() {
        int blockLength = this.chain.size();
        if (blockLength > 0) {
            return this.chain.get(blockLength - 1);
        }

        return null;
    }
}
