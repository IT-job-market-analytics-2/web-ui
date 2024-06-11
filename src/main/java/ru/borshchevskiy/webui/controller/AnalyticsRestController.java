package ru.borshchevskiy.webui.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.webui.dto.analytics.AnalyticsDto;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.service.RestApiClientService;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsRestController {

    private final RestApiClientService restApiClientService;

    public AnalyticsRestController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping(value = "/queries", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getAvailableQueries() {
        return restApiClientService.getaAvailableQueries();
    }

    @GetMapping(value = "/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<AnalyticsDto> getAnalyticsByQuery(@PathVariable("query") String query) {
        return restApiClientService.getAnalyticsByQuery(query);
    }

    @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<AnalyticsDto> getRecentAnalytics() {
        return restApiClientService.getRecentAnalytics();
    }
}
