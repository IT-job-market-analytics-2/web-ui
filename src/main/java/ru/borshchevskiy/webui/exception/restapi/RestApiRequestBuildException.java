package ru.borshchevskiy.webui.exception.restapi;

import java.util.List;

public class RestApiRequestBuildException extends RestApiException{
    public RestApiRequestBuildException() {
    }

    public RestApiRequestBuildException(String message) {
        super(message);
    }

    public RestApiRequestBuildException(List<String> messages) {
        super(messages);
    }

    public RestApiRequestBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiRequestBuildException(List<String> messages, Throwable cause) {
        super(messages, cause);
    }

    public RestApiRequestBuildException(Throwable cause) {
        super(cause);
    }

    public RestApiRequestBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
