package ru.borshchevskiy.webui.exception.restapi;

import java.util.List;

public class RestApiResponseReadException extends RestApiException{
    public RestApiResponseReadException() {
    }

    public RestApiResponseReadException(String message) {
        super(message);
    }

    public RestApiResponseReadException(List<String> messages) {
        super(messages);
    }

    public RestApiResponseReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiResponseReadException(List<String> messages, Throwable cause) {
        super(messages, cause);
    }

    public RestApiResponseReadException(Throwable cause) {
        super(cause);
    }

    public RestApiResponseReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
