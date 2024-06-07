package com.test.peer;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import com.test.network.ConnectionHeader;


public class WebSocketPeer extends Peer {
    public WebSocketPeer(Socket socket, ConnectionHeader header) throws IOException {
        super(socket, header);
    }

    @Override()
    public void initiateHandshake() {
        try {
            byte[] acceptKeyBytes = this.header.getHeader("Sec-WebSocket-Key")
                .concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                .getBytes();
            String acceptKey = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(acceptKeyBytes)
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
    protected String processBytesRead(byte[] buffer, int length) {
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

    @Override()
    public void sendData(String data) throws IOException {
        byte[] stringBytes = data.getBytes();

        int newLength = 2 + stringBytes.length;
        int startIndex = 2;

        // According to websocket protocol. If the length is greater than 125,
        // We need to use extended payload lengths.
        if (stringBytes.length > 125) {
            newLength += 2;
            startIndex += 2;
        }

        byte[] bytes = new byte[newLength];
        
        bytes[0] = (byte) 0x81;

        if (stringBytes.length <= 126) {
            bytes[1] = (byte) (stringBytes.length & 0x7F);
        } else {
            bytes[1] = 126;
            bytes[2] = (byte) ((stringBytes.length >> 8) * 0xFF);
            bytes[3] = (byte) (stringBytes.length & 0xFF);
        }

        for (int i = startIndex; i < newLength; i++) {
            bytes[i] += stringBytes[i - startIndex];
        }

        this.dos.write(bytes);
        this.dos.flush();
    }

}
