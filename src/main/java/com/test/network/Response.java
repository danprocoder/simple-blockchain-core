package com.test.network;

public class Response {

    private final short status;

    private final String statusText;

    public Response(short status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }
    
    @Override()
    public String toString() {
        return this.status + " " + this.statusText;
    }
}
