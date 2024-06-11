package ru.borshchevskiy.webui.exception.restapi;

import java.util.List;

public class RestApiClientErrorException extends RestApiException {
    public RestApiClientErrorException() {
    }

    public RestApiClientErrorException(String message) {
        super(message);
    }

    public RestApiClientErrorException(List<String> messages) {
        super(messages);
    }

    public RestApiClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiClientErrorException(List<String> messages, Throwable cause) {
        super(messages, cause);
    }

    public RestApiClientErrorException(Throwable cause) {
        super(cause);
    }

    public RestApiClientErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
