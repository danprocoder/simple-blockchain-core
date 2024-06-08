package com.test.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Peer extends Thread {
    protected final Socket socket;
    protected final ConnectionContext context;

    protected DataOutputStream dos;

    public Peer(Socket socket) throws IOException {
        this.socket = socket;
        this.context = new ConnectionContext(socket);

        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override()
    public void run() {
        this.fetchMessages();
    }

    public void initiateHandshake() throws Exception {
        if (this.isWebSocket()) {
            byte[] acceptKeyBytes = this.context.getHeader("Sec-WebSocket-Key")
                .concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                .getBytes();
            String acceptKey = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(acceptKeyBytes)
            );

            String responseHeader = "HTTP/1.1 101 Switching Protocols\r\n"
                + "Upgrade: websocket\r\n"
                + "Connection: Upgrade\r\n"
                + "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";

            // This calls the write() method of dos directly because websocket clients are not expecting
            // the handshake response to be a framed data.
            this.dos.write(responseHeader.getBytes());
            this.dos.flush();
        }
    }

    public ConnectionContext getContext() {
        return this.context;
    }

    public boolean isMiner() {
        return this.context.getClientType().equals("miner");
    }

    public boolean isNode() {
        return this.context.getClientType().equals("node");
    }

    public boolean isWallet() {
        return this.context.getClientType().equals("wallet");
    }

    public void sendData(byte[] b) throws IOException {
        if (this.isWebSocket()) {
            byte[] wsFrame = this.constructWebSocketFrame(b);
            int CHUNK_SIZE = 64;
    
            for (int i = 0; i < wsFrame.length; i += CHUNK_SIZE) {
                int length = Math.min(CHUNK_SIZE, wsFrame.length - i);
                this.dos.write(wsFrame, i, length);
                this.dos.flush();
            }
        } else {
            this.dos.write(b);
            this.dos.flush();
        }
    }

    public void sendData(Message response) throws IOException {
        this.sendData(response.toString().getBytes());
    }

    /**
     * Starts an endless pooling for new messages from the client. When a message is received,
     * is calls the request handler to do parse the message and call the appropriate controller.
     */
    private void fetchMessages() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = this.socket.getInputStream().read(buffer)) != -1) {
                String message = this.processBytesRead(buffer, bytesRead);

                RequestHandler requestHandler = RequestHandler.getInstance();
                requestHandler.resolve(this, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processBytesRead(byte[] buffer, int length) {
        if (this.isWebSocket()) {
            return this.processWebSocketFrame(buffer, length);
        }

        return new String(Arrays.copyOfRange(buffer, 0, length));
    }

    /**
     * Deconstructs the frame sent by websocket clients and returns the body of the
     * message as a string.
     *
     * @param buffer
     * @param length
     * @return
     */
    private String processWebSocketFrame(byte[] buffer, int length) {
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

    /**
     * Constructors a websocket frame to send to clients connected via websocket.
     *
     * @param stringBytes
     * @return
     */
    private byte[] constructWebSocketFrame(byte[] stringBytes) {
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

        if (stringBytes.length <= 125) {
            bytes[1] = (byte) (stringBytes.length & 0x7F);
        } else {
            bytes[1] = 126;
            bytes[2] = (byte) ((stringBytes.length >> 8) & 0xFF);
            bytes[3] = (byte) (stringBytes.length & 0xFF);
        }

        for (int i = startIndex; i < newLength; i++) {
            bytes[i] += stringBytes[i - startIndex];
        }

        return bytes;
    }

    private boolean isWebSocket() {
        return this.context.getSource().equals("websocket");
    }
}
