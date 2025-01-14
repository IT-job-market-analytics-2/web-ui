package ru.borshchevskiy.webui.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.borshchevskiy.webui.controller.AnalyticsRestController;

@Component
public class RestApiUriProvider {

    @Value("${rest-api.scheme}")
    private String restApiScheme;
    @Value("${rest-api.port}")
    private String restApiPort;
    @Value("${rest-api.host}")
    private String restApiHost;
    @Value("${rest-api.endpoints.auth.sign-up:/auth/registration}")
    private String signUpEndpoint;
    @Value("${rest-api.endpoints.auth.sign-in:/auth/login}")
    private String signInEndpoint;
    @Value("${rest-api.endpoints.auth.refresh:/auth/refresh}")
    private String tokenRefreshEndpoint;
    @Value("${rest-api.endpoints.users.user:/users/getUserInfo}")
    private String userEndpoint;
    @Value("${rest-api.endpoints.users.update:/users}")
    private String updateUserEndpoint;
    @Value("${rest-api.endpoints.subscriptions.current:/subscriptions}")
    private String currentSubscriptionsEndpoint;
    @Value("${rest-api.endpoints.subscriptions.available:/subscriptions/allAvailable}")
    private String availableSubscriptionsEndpoint;
    @Value("${rest-api.endpoints.subscriptions.add:/subscriptions}")
    private String addSubscriptionEndpoint;
    @Value("${rest-api.endpoints.subscriptions.remove:/subscriptions}")
    private String removeSubscriptionEndpoint;
    @Value("${rest-api.endpoints.analytics.by-query:/analytics/history/{query}?depth={depth}}")
    private String analyticsByQueryEndpoint;
    @Value("${rest-api.endpoints.analytics.recent:/analytics/byQuery}")
    private String recentAnalyticsEndpoint;
    private String rootUri;
    private String signUpUri;
    private String signInUri;
    private String tokenRefreshUri;
    private String userUri;
    private String updateUserUri;
    private String currentSubscriptionsUri;
    private String availableSubscriptionsUri;
    private String addSubscriptionUri;
    private String removeSubscriptionUri;
    private String analyticsByQueryUri;
    private String recentAnalyticsUri;
    private static final Logger log = LoggerFactory.getLogger(RestApiUriProvider.class);
    @PostConstruct
    private void init() {
        log.debug("Initializing REST API URIs.");
        this.rootUri = UriComponentsBuilder.newInstance()
                .scheme(restApiScheme)
                .host(restApiHost)
                .port(restApiPort)
                .build()
                .toUriString();
        log.debug("REST API root URI initialized - {}", rootUri);
        this.signUpUri = buildUriFromRoot(signUpEndpoint);
        log.debug("REST API sign-up URI initialized - {}", signUpUri);
        this.signInUri = buildUriFromRoot(signInEndpoint);
        log.debug("REST API sign-in URI initialized - {}", signInUri);
        this.tokenRefreshUri = buildUriFromRoot(tokenRefreshEndpoint);
        log.debug("REST API token refresh URI initialized - {}", tokenRefreshUri);
        this.userUri = buildUriFromRoot(userEndpoint);
        log.debug("REST API user URI initialized - {}", userUri);
        this.updateUserUri = buildUriFromRoot(updateUserEndpoint);
        log.debug("REST API user update URI initialized - {}", updateUserUri);
        this.currentSubscriptionsUri = buildUriFromRoot(currentSubscriptionsEndpoint);
        log.debug("REST API user's current subscriptions URI initialized - {}", currentSubscriptionsUri);
        this.availableSubscriptionsUri = buildUriFromRoot(availableSubscriptionsEndpoint);
        log.debug("REST API all available subscriptions URI initialized - {}", availableSubscriptionsUri);
        this.addSubscriptionUri = buildUriFromRoot(addSubscriptionEndpoint);
        log.debug("REST API add user subscription URI initialized - {}", addSubscriptionUri);
        this.removeSubscriptionUri = buildUriFromRoot(removeSubscriptionEndpoint);
        log.debug("REST API remove user subscription URI initialized - {}", removeSubscriptionUri);
        this.analyticsByQueryUri = buildUriFromRoot(analyticsByQueryEndpoint);
        log.debug("REST API get analytics by query URI initialized - {}", analyticsByQueryUri);
        this.recentAnalyticsUri = buildUriFromRoot(recentAnalyticsEndpoint);
        log.debug("REST API get recent analytics URI initialized - {}", recentAnalyticsUri);
    }

    private String buildUriFromRoot(String path) {
        return UriComponentsBuilder.fromUriString(rootUri)
                .path(path)
                .build()
                .toUriString();
    }

    private String addPathSegment(String uri, String path) {
        return UriComponentsBuilder.fromUriString(uri)
                .pathSegment(path)
                .build()
                .toUriString();
    }

    public String getSignUpUri() {
        return signUpUri;
    }

    public String getSignInUri() {
        return signInUri;
    }

    public String getTokenRefreshUri() {
        return tokenRefreshUri;
    }

    public String getUserUri() {
        return userUri;
    }

    public String getUpdateUserUri() {
        return updateUserUri;
    }

    public String getCurrentSubscriptionsUri() {
        return currentSubscriptionsUri;
    }

    public String getAvailableSubscriptionsUri() {
        return availableSubscriptionsUri;
    }

    public String getAddSubscriptionUri(String subscription) {
        return addPathSegment(addSubscriptionUri, subscription);
    }

    public String getRemoveSubscriptionUri(String subscription) {
        return addPathSegment(removeSubscriptionUri, subscription);
    }

    public String getAnalyticsByQueryUri() {
        return analyticsByQueryUri;
    }

    public String getRecentAnalyticsUri() {
        return recentAnalyticsUri;
    }
}
