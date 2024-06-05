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
    @Value("${rest-api.endpoints.users.user:/users/getUserInfo}")
    private String userEndpoint;
    @Value("${rest-api.endpoints.users.update:/users}")
    private String updateUserEndpoint;
    @Value("${rest-api.endpoints.subscriptions.current:/subscriptions}")
    private String currentSubscriptions;
    @Value("${rest-api.endpoints.subscriptions.available:/subscriptions/allAvailable}")
    private String availableSubscriptions;
    private String rootUri;
    private String signUpUri;
    private String signInUri;
    private String userUri;
    private String updateUserUri;
    private String currentSubscriptionsUri;
    private String availableSubscriptionsUri;

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
        this.userUri = buildUriFromRoot(userEndpoint);
        this.updateUserUri = buildUriFromRoot(updateUserEndpoint);
        this.currentSubscriptionsUri = buildUriFromRoot(currentSubscriptions);
        this.availableSubscriptionsUri = buildUriFromRoot(availableSubscriptions);
    }

    private String buildUriFromRoot(String path) {
        return UriComponentsBuilder.fromUriString(rootUri)
                .path(path)
                .build()
                .toUriString();
    }

    public String getSignUpUri() {
        return signUpUri;
    }

    public String getSignInUri() {
        return signInUri;
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
}
