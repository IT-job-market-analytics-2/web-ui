package ru.borshchevskiy.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.service.RestApiClientService;

@Controller
@RequestMapping("/sign-in")
public class SignInController {

    private final RestApiClientService restApiClientService;

    public SignInController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping
    public String getSignIn(Model model) {
        model.addAttribute("user", new SignInDto());
        return "signin";
    }

    @PostMapping
    public String performSignIn(SignInDto signInDto, Model model) {
        SignInResponseDto signInResponseDto = restApiClientService.signIn(signInDto);
        return "redirect:/";
    }
}
