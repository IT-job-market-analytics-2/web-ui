package ru.borshchevskiy.webui.controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.dto.validation.groups.OnUpdate;
import ru.borshchevskiy.webui.exception.restapi.RestApiClientErrorException;
import ru.borshchevskiy.webui.exception.restapi.RestApiRequestBuildException;
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
        model.addAttribute("subscription", new SubscriptionDto());
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute @Validated(OnUpdate.class) UserDto user,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            redirectAttributes.addFlashAttribute("updateErrorMessages", errorMessages);
            return "redirect:/profile";
        }
        restApiClientService.updateUser(user);
        redirectAttributes.addFlashAttribute("isProfileUpdated", true);
        return "redirect:/profile";
    }

    @PostMapping("/subscriptions/add")
    public String addSubscription(@ModelAttribute @Validated(OnUpdate.class) SubscriptionDto query,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            redirectAttributes.addFlashAttribute("addSubscriptionErrorMessages", errorMessages);
            return "redirect:/profile";
        }
        restApiClientService.addSubscription(query.getQuery());
        return "redirect:/profile";
    }

    @PostMapping("/subscriptions/remove")
    public String removeSubscription(@ModelAttribute @Validated(OnUpdate.class) SubscriptionDto query,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            redirectAttributes.addFlashAttribute("removeSubscriptionErrorMessages", errorMessages);
            return "redirect:/profile";
        }
        restApiClientService.removeSubscription(query.getQuery());
        return "redirect:/profile";
    }

    @ExceptionHandler(RestApiRequestBuildException.class)
    public String RestApiRequestBuildException(RestApiRequestBuildException exception,
                                               RedirectAttributes redirectAttributes) {
        List<String> errors = exception.getMessages();
        redirectAttributes.addFlashAttribute("errorMessages", errors);
        return "redirect:/profile";
    }

    @ExceptionHandler(RestApiClientErrorException.class)
    public String handleRestApiClientErrorException(RestApiClientErrorException exception,
                                                    RedirectAttributes redirectAttributes) {
        List<String> errors = exception.getMessages();
        redirectAttributes.addFlashAttribute("errorMessages", errors);
        return "redirect:/profile";
    }
}
