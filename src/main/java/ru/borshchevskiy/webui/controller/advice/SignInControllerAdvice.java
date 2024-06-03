package ru.borshchevskiy.webui.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.controller.SignInController;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;

@ControllerAdvice(assignableTypes = SignInController.class)
public class SignInControllerAdvice {

    @ExceptionHandler(RestApiException.class)
    public String handleRestApiException(RestApiException exception,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        SignInDto user = new SignInDto();
        String username = request.getParameter("username");
        user.setUsername(username);
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/sign-in";
    }
}
