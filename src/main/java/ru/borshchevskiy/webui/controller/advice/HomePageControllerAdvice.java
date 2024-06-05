package ru.borshchevskiy.webui.controller.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.borshchevskiy.webui.controller.HomePageController;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import java.util.List;

@ControllerAdvice(assignableTypes = HomePageController.class)
public class HomePageControllerAdvice {

    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException(Model model) {
        model.addAttribute("isLoggedIn", false);
        return "index";
    }

    @ExceptionHandler(RestApiException.class)
    public String handleRestApiException(RestApiException exception,
                                         Model model) {
        List<String> errors = exception.getMessages();
        model.addAttribute("isLoggedIn", false);
        model.addAttribute("errorMessages", errors);
        return "index";
    }
}
