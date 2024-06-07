package com.test.network;

public class Response {

    private final int status;

    private final String statusText;

    private String body;

    public Response(int status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    @Override()
    public String toString() {
        String value = this.status + " " + this.statusText;
        if (this.body != null) {
            value += "\r\n\r\n" + this.body;
        }
        return value;
    }
}
