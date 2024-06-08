package com.test.network;


public class RequestException extends Exception {

    private final Message message;
    
    public RequestException(String message, Message msg) {
        super(message);

        this.message = msg;
    }

    @Override()
    public String getMessage() {
        Message error = new Message();

        String id = this.message.getId();
        if (id != null) {
            error.setId(id);
        }

        String event = this.message.getHeader("event");
        if (event != null) {
            error.addHeader("event", event);
        }

        error.addHeader("Content-Type", "text/plain");
        // TODO: Implement different error codes.
        error.addHeader("Error-Code", 0);
        error.setBody(super.getMessage());

        return error.toString();
    }
}
