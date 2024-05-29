package ru.borshchevskiy.webui.exception.restapi;

public class RestApiUnauthorizedException extends RestApiException{
    public RestApiUnauthorizedException() {
    }

    public RestApiUnauthorizedException(String message) {
        super(message);
    }

    public RestApiUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiUnauthorizedException(Throwable cause) {
        super(cause);
    }

    public RestApiUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
