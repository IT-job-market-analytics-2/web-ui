package ru.borshchevskiy.webui.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

@ControllerAdvice(assignableTypes = ProfileControllerAdvice.class)
public class ProfileControllerAdvice {

    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException() {
        return "redirect:/sign-in";
    }
}
