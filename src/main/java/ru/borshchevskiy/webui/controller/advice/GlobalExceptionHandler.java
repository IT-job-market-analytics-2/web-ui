package ru.borshchevskiy.webui.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.exception.restapi.RestApiClientErrorException;
import ru.borshchevskiy.webui.exception.restapi.RestApiServerErrorException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException(RedirectAttributes redirectAttributes) {
        String notAuthorizedMessage = "Not authorized. Please, sign in.";
        redirectAttributes.addFlashAttribute("errorMessages", notAuthorizedMessage);
        return "redirect:/sign-in";
    }

    @ExceptionHandler(RestApiClientErrorException.class)
    public String handleRestApiClientErrorException(RestApiClientErrorException exception,
                                                    RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/";
    }

    @ExceptionHandler(RestApiServerErrorException.class)
    public String handleRestApiServerErrorException(RestApiServerErrorException exception,
                                                    RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/";
    }
}
