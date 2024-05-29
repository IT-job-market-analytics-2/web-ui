package ru.borshchevskiy.webui.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.controller.SignUpController;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;

@ControllerAdvice(assignableTypes = SignUpController.class)
public class SignUpControllerAdvice {

    @ExceptionHandler(RestApiException.class)
    public String handleRestApiException(RestApiException exception,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        UserDto user = new UserDto();
        String username = request.getParameter("username");
        user.setUsername(username);
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        return "redirect:/sign-up";
    }
}
