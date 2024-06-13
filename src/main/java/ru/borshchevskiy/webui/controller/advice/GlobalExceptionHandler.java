package ru.borshchevskiy.webui.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.exception.restapi.RestApiClientErrorException;
import ru.borshchevskiy.webui.exception.restapi.RestApiResponseReadException;
import ru.borshchevskiy.webui.exception.restapi.RestApiServerErrorException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;
import ru.borshchevskiy.webui.service.RestApiClientService;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(RestApiUnauthorizedException.class)
    public String handleRestApiUnauthorizedException(RedirectAttributes redirectAttributes) {
        log.debug("Handling RestApiUnauthorizedException, redirecting to sign-in page.");
        String notAuthorizedMessage = "Not authorized. Please, sign in.";
        redirectAttributes.addFlashAttribute("errorMessages", notAuthorizedMessage);
        return "redirect:/sign-in";
    }

    @ExceptionHandler(RestApiClientErrorException.class)
    public String handleRestApiClientErrorException(RestApiClientErrorException exception,
                                                    RedirectAttributes redirectAttributes) {
        log.debug("Handling RestApiClientErrorException, redirecting to main page.");
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/";
    }

    @ExceptionHandler(RestApiServerErrorException.class)
    public String handleRestApiServerErrorException(RestApiServerErrorException exception,
                                                    RedirectAttributes redirectAttributes) {
        log.debug("Handling RestApiServerErrorException, redirecting to main page.");
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/";
    }

    @ExceptionHandler(RestApiResponseReadException.class)
    public ModelAndView RestApiResponseReadException(RestApiResponseReadException exception) {
        log.debug("Handling RestApiResponseReadException, sending to main page ModelAndView.");
        ModelAndView modelAndView = new ModelAndView();
        List<String> errors = exception.getMessages();
        modelAndView.setViewName("index");
        modelAndView.addObject("isLoggedIn", false);
        modelAndView.addObject("errorMessages", errors);
        return modelAndView;
    }
}
