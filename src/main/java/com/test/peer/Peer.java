package com.test.peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

public abstract class Peer extends Thread {
    protected final Socket socket;
    protected final ConnectionHeader header;
    protected final ServerListener listener;

    protected BufferedReader reader;
    protected DataOutputStream dos;

    public Peer(Socket socket, ConnectionHeader header, ServerListener listener) throws IOException {
        this.socket = socket;
        this.header = header;
        this.listener = listener;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override()
    public void run() {
        this.fetchMessages();
    }

    public void sendData(String payload) throws IOException {
        this.dos.write(payload.getBytes());
        this.dos.flush();
    }

    protected void fetchMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = this.socket.getInputStream().read(buffer)) != -1) {
                this.onMessageFetched(Arrays.copyOfRange(buffer, 0, bytesRead), bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onMessageFetched(byte[] bytes, int length);
}
