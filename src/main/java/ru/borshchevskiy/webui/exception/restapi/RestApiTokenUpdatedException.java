package ru.borshchevskiy.webui.exception.restapi;

import java.util.List;

public class RestApiTokenUpdatedException extends RestApiException{
    public RestApiTokenUpdatedException() {
    }

    public RestApiTokenUpdatedException(String message) {
        super(message);
    }

    public RestApiTokenUpdatedException(List<String> messages) {
        super(messages);
    }

    public RestApiTokenUpdatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiTokenUpdatedException(List<String> messages, Throwable cause) {
        super(messages, cause);
    }

    public RestApiTokenUpdatedException(Throwable cause) {
        super(cause);
    }

    public RestApiTokenUpdatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
