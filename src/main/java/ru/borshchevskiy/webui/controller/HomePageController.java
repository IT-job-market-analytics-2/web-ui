package ru.borshchevskiy.webui.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.service.RestApiClientService;

@Controller
@RequestMapping("/")
public class HomePageController {

    private final RestApiClientService restApiClientService;

    public HomePageController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping
    public String getMainPage(Model model) {
        UserDto user = restApiClientService.getUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("isLoggedIn", true);
        return "index";
    }
}
