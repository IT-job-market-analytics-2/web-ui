package ru.borshchevskiy.webui.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.service.RestApiClientService;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    private final RestApiClientService restApiClientService;

    public SignUpController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping
    public String getSignUp(Model model) {
        model.addAttribute("user", new SignUpDto());
        return "signup";
    }

    @PostMapping
    public String performSignUp(SignUpDto signUpDto) {
        restApiClientService.signUp(signUpDto);
        return "redirect:/sign-in";
    }

    @ExceptionHandler(RestApiException.class)
    public String handleRestApiException(RestApiException exception,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        SignUpDto user = new SignUpDto();
        String username = request.getParameter("username");
        user.setUsername(username);
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/sign-up";
    }
}
