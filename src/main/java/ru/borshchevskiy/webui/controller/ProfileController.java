package ru.borshchevskiy.webui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.dto.validation.groups.OnUpdate;
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

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute @Validated(OnUpdate.class) UserDto user,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            redirectAttributes.addFlashAttribute("updateErrorMessages", errorMessages);
            return "redirect:/profile";
        }
        UserDto updatedUser = restApiClientService.updateUser(user);
        model.addAttribute("user", updatedUser);
        return "redirect:/profile";
    }


}
