package com.test.core;

public class Coin {
    final double TOTAL_SUPPLY = 1500000000;

    /**
     * transaction fee + mining reward per block.
     */
    public double calculateMinersReward() {
        return 4;
    }
}
