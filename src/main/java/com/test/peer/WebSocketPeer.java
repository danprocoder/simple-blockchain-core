package com.test.peer;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.test.dto.Transaction;
import com.test.node.ConnectionHeader;
import com.test.node.ServerListener;

class Payload {
    public String action;
    public Object data;
}

public class WebSocketPeer extends Peer {
    public WebSocketPeer(Socket socket, ConnectionHeader header, ServerListener listener) throws IOException {
        super(socket, header, listener);
    }

    @Override()
    public void run() {
        this.initiateHandshake();

        super.run();
    }

    private void initiateHandshake() {
        try {
            String acceptKey = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(
                    this.header
                        .getHeader("Sec-WebSocket-Key")
                        .concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                        .getBytes()
                )
            );

            String responseHeader = "HTTP/1.1 101 Switching Protocols\r\n"
                + "Upgrade: websocket\r\n"
                + "Connection: Upgrade\r\n"
                + "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";

            super.sendData(responseHeader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override()
    protected void onMessageFetched(byte[] bytes, int length) {
        String message = this.parseWebSocketFrame(bytes, length);
        Payload payload = new Gson().fromJson(message, Payload.class);

        LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) payload.data;
        if (payload.action.equals("transaction")) {
            Transaction trx = new Transaction(
                (String) data.get("from"),
                (String) data.get("to"),
                (Double) data.get("amount"),
                ((Double) data.get("timestamp")).longValue(),
                (String) data.get("signature")
            );
            this.listener.onTransactionReceived(trx, this, message);
        }
    }

    @Override()
    public void sendData(String data) throws IOException {
        byte[] stringBytes = data.getBytes();

        int newLength = 2 + stringBytes.length;
        byte[] bytes = new byte[newLength];
        
        bytes[0] = (byte) 0x81;
        bytes[1] = (byte) (stringBytes.length & 0x7F);

        for (int i = 2; i < newLength; i++) {
            bytes[i] += stringBytes[i - 2];
        }

        this.dos.write(bytes);
        this.dos.flush();
    }

    private String parseWebSocketFrame(byte[] buffer, int length) {
        boolean isMasked = ((buffer[1] & 0x80) >> 7) == 1;
        int payloadLength = buffer[1] & 0x7F;
        int payloadStartIndex = 2;

        if (payloadLength == 126) {
            payloadLength = ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
            payloadStartIndex += 2;
        }

        byte[] maskKey = new byte[4];
        if (isMasked) {
            maskKey = Arrays.copyOfRange(buffer, payloadStartIndex, payloadStartIndex + 4);
            payloadStartIndex += 4;
        }

        byte[] bytes = new byte[payloadLength];
        for (int i = payloadStartIndex; i < length; i++) {
            int writeIndex = i - payloadStartIndex;

            if (isMasked) {
                bytes[writeIndex] = (byte) (buffer[i] ^ (maskKey[writeIndex % 4]));
            } else {
                bytes[writeIndex] = buffer[i];
            }
        }

        return new String(bytes);
    }
}
