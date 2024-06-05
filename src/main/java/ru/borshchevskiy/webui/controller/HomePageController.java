package ru.borshchevskiy.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;
import ru.borshchevskiy.webui.service.RestApiClientService;

import java.util.List;

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

    @ExceptionHandler(RestApiUnauthorizedException.class)
    public ModelAndView handleRestApiUnauthorizedException(RestApiUnauthorizedException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("isLoggedIn", false);
        return modelAndView;
    }

    @ExceptionHandler(RestApiException.class)
    public ModelAndView handleRestApiException(RestApiException exception) {
        ModelAndView modelAndView = new ModelAndView();
        List<String> errors = exception.getMessages();
        modelAndView.setViewName("index");
        modelAndView.addObject("isLoggedIn", false);
        modelAndView.addObject("errorMessages", errors);
        return modelAndView;
    }
}
