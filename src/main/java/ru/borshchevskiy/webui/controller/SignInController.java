package ru.borshchevskiy.webui.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.service.RestApiClientService;

@Controller
@RequestMapping("/sign-in")
public class SignInController {

    private final RestApiClientService restApiClientService;
    private static final Logger log = LoggerFactory.getLogger(SignInController.class);

    public SignInController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping
    public String getSignIn(Model model) {
        model.addAttribute("user", new SignInDto());
        return "signin";
    }

    @PostMapping
    public String performSignIn(SignInDto signInDto, HttpSession session) {
        log.debug("Starting sign-in for username {}.", signInDto.getUsername());
        SignInResponseDto signInResponseDto = restApiClientService.signIn(signInDto);
        session.setAttribute("accessToken", signInResponseDto.getAccessToken());
        session.setAttribute("refreshToken", signInResponseDto.getRefreshToken());
        return "redirect:/";
    }

    @ExceptionHandler(RestApiException.class)
    public String handleRestApiException(RestApiException exception,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        SignInDto user = new SignInDto();
        String username = request.getParameter("username");
        log.debug("Sign-in with username " + username + " failed with exception.", exception);
        user.setUsername(username);
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("errorMessages", exception.getMessages());
        return "redirect:/sign-in";
    }
}

