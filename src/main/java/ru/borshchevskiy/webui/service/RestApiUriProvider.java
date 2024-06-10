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
    private String currentSubscriptions;
    @Value("${rest-api.endpoints.subscriptions.available:/subscriptions/allAvailable}")
    private String availableSubscriptions;
    @Value("${rest-api.endpoints.subscriptions.add:/subscriptions}")
    private String addSubscription;
    @Value("${rest-api.endpoints.subscriptions.remove:/subscriptions}")
    private String removeSubscription;
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
        this.currentSubscriptionsUri = buildUriFromRoot(currentSubscriptions);
        this.availableSubscriptionsUri = buildUriFromRoot(availableSubscriptions);
        this.addSubscriptionUri = buildUriFromRoot(addSubscription);
        this.removeSubscriptionUri = buildUriFromRoot(removeSubscription);
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
}
