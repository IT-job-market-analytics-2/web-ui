package ru.borshchevskiy.webui.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException(RestApiUnauthorizedException e) {
        return "redirect:/sign-in";
    }
}
