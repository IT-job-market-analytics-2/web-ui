package ru.borshchevskiy.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
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
    public String performSignUp(SignUpDto signUpDto, Model model) {
        restApiClientService.signUp(signUpDto);
        return "redirect:/sign-in";
    }
}
