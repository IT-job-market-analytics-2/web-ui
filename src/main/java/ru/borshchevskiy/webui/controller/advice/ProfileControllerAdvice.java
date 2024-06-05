package ru.borshchevskiy.webui.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.controller.ProfileController;
import ru.borshchevskiy.webui.exception.restapi.RestApiClientErrorException;
import ru.borshchevskiy.webui.exception.restapi.RestApiRequestBuildException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import java.util.List;

@ControllerAdvice(assignableTypes = ProfileController.class)
public class ProfileControllerAdvice {

    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessages", "Not authorized. Please, sign in.");
        return "redirect:/sign-in";
    }

    @ExceptionHandler(RestApiRequestBuildException.class)
    public String RestApiRequestBuildException(RestApiRequestBuildException exception,
                                               RedirectAttributes redirectAttributes) {
        List<String> errors = exception.getMessages();
        redirectAttributes.addFlashAttribute("errorMessages", errors);
        return "redirect:/profile";
    }

    @ExceptionHandler(RestApiClientErrorException.class)
    public String handleRestApiClientErrorException(RestApiClientErrorException exception,
                                                    RedirectAttributes redirectAttributes) {
        List<String> errors = exception.getMessages();
        redirectAttributes.addFlashAttribute("errorMessages", errors);
        return "redirect:/profile";
    }
}
