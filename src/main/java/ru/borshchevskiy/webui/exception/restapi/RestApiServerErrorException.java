package ru.borshchevskiy.webui.exception.restapi;

import java.util.List;

public class RestApiServerErrorException extends RestApiException{
    public RestApiServerErrorException() {
    }

    public RestApiServerErrorException(String message) {
        super(message);
    }

    public RestApiServerErrorException(List<String> messages) {
        super(messages);
    }

    public RestApiServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiServerErrorException(List<String> messages, Throwable cause) {
        super(messages, cause);
    }

    public RestApiServerErrorException(Throwable cause) {
        super(cause);
    }

    public RestApiServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
