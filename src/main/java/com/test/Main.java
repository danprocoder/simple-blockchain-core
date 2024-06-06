package com.test;

import java.io.IOException;

import com.test.node.Server;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = Server.getInstance();

            server.listenForConnections(Integer.parseInt(args[0]));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
