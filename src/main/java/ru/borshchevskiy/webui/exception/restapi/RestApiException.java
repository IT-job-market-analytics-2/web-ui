package ru.borshchevskiy.webui.exception.restapi;

import java.util.List;

public class RestApiException extends RuntimeException {

    private List<String> messages;

    public RestApiException() {
    }

    public RestApiException(String message) {
        super(message);
    }

    public RestApiException(List<String> messages) {
        super(messages.toString());
        this.messages = messages;
    }

    public RestApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiException(List<String> messages, Throwable cause) {
        super(messages.toString(), cause);
        this.messages = messages;
    }

    public RestApiException(Throwable cause) {
        super(cause);
    }

    public RestApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
