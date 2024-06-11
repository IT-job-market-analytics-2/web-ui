package ru.borshchevskiy.webui.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PostConstruct
    private void init() {
        this.rootUri = UriComponentsBuilder.newInstance()
                .scheme(restApiScheme)
                .host(restApiHost)
                .port(restApiPort)
                .build()
                .toUriString();
        this.signUpUri = buildUriFromRoot(signUpEndpoint);
        this.signInUri = buildUriFromRoot(signInEndpoint);
        this.tokenRefreshUri = buildUriFromRoot(tokenRefreshEndpoint);
        this.userUri = buildUriFromRoot(userEndpoint);
        this.updateUserUri = buildUriFromRoot(updateUserEndpoint);
        this.currentSubscriptionsUri = buildUriFromRoot(currentSubscriptionsEndpoint);
        this.availableSubscriptionsUri = buildUriFromRoot(availableSubscriptionsEndpoint);
        this.addSubscriptionUri = buildUriFromRoot(addSubscriptionEndpoint);
        this.removeSubscriptionUri = buildUriFromRoot(removeSubscriptionEndpoint);
        this.analyticsByQueryUri = buildUriFromRoot(analyticsByQueryEndpoint);
        this.recentAnalyticsUri = buildUriFromRoot(recentAnalyticsEndpoint);
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
