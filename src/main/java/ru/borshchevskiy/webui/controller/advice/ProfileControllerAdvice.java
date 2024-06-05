package ru.borshchevskiy.webui.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.controller.ProfileController;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import java.util.List;

@ControllerAdvice(assignableTypes = ProfileController.class)
public class ProfileControllerAdvice {

    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException() {
        return "redirect:/sign-in";
    }

    @ExceptionHandler(RestApiException.class)
    public String handleRestApiException(RestApiException exception,
                                         RedirectAttributes redirectAttributes) {
        List<String> errors = exception.getMessages();
        redirectAttributes.addFlashAttribute("errorMessages", errors);
        return "redirect:/";
    }
}
