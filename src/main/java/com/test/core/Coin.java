package com.test.core;

public class Coin {
    public final static double TOTAL_SUPPLY = 1500000000;

    public final static String GENESIS_WALLET_ADDRESS = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANr06tKOATfgwvQyGNqq2faXfJfIg9QfYci9MSQemFvUUTdlMCx6/mW9P04XeoPOj4K+mFK+IJGzBKFBIE4xy9MCAwEAAQ==";

    public final static String GENESIS_SECRET_KEY = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA2vTq0o4BN+DC9DIY2qrZ9pd8l8iD1B9hyL0xJB6YW9RRN2UwLHr+Zb0/Thd6g86Pgr6YUr4gkbMEoUEgTjHL0wIDAQABAkBxCcQ5U4qZeHXtb/eY3F+OiQKPsbstRc5LvjCifxEVRoDl2PDWCW0q1eib32A2KXyaGnnY4BTIDXn2CvQRsfyhAiEA8uYbBxPv3NYBs3dxZGz1GrLafhGGbMQzcjYdOaTdnhsCIQDmxDlMKoTnBrKjJP2se0mZWHSeyrqiFNEbW6cOKwQEqQIhANVN3U5J48o65SOFML7QMC5SAi3TlgjOA5+4hdGpRjUhAiBsVQXI+dT2V7CY4g6sYBxG/r2Qpf9Dg54+x6H/BraWMQIhALnG+OrZYMaW0OLKJyhvlbD6VjwvZbJ0nTLZ7o8yBtF+";

    /**
     * transaction fee + mining reward per block.
     */
    public static double calculateMinersReward() {
        return 4;
    }
}
