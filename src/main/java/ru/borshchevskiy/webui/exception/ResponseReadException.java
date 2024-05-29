package ru.borshchevskiy.webui.exception;

public class ResponseReadException extends RuntimeException{
    public ResponseReadException() {
    }

    public ResponseReadException(String message) {
        super(message);
    }

    public ResponseReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseReadException(Throwable cause) {
        super(cause);
    }

    public ResponseReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
