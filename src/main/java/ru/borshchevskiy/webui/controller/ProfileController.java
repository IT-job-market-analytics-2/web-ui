package ru.borshchevskiy.webui.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.service.RestApiClientService;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final RestApiClientService restApiClientService;

    public ProfileController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping
    public String getProfile(Model model) {
        UserDto user = restApiClientService.getUser();
        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", true);
        List<SubscriptionDto> currentSubscriptions = restApiClientService.getCurrentSubscriptions();
        List<SubscriptionDto> availableSubscriptions = restApiClientService.getaAvailableSubscriptions();
        availableSubscriptions.removeAll(currentSubscriptions);
        model.addAttribute("currentSubscriptions", currentSubscriptions);
        model.addAttribute("availableSubscriptions", availableSubscriptions);
        model.addAttribute("updateSubscription", new SubscriptionDto());
        return "profile";
    }
}
