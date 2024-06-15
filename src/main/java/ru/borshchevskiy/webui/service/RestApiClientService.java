package ru.borshchevskiy.webui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.borshchevskiy.webui.dto.analytics.AnalyticsDto;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.dto.error.ErrorResponseDto;
import ru.borshchevskiy.webui.dto.error.Violation;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class RestApiClientService {

    private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.";
    private static final String ACCESS_TOKEN_NAME = "accessToken";
    private static final String REFRESH_TOKEN_NAME = "refreshToken";
    private final ExecutorService tokenRefreshWorkersPool = Executors.newCachedThreadPool();
    @Value("${rest-api.tokens.refresh-wait-timeout:5}")
    private Long tokensRefreshWaitTimeout;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final RestApiUriProvider restApiUriProvider;

    private final HttpSession session;
    private static final Logger log = LoggerFactory.getLogger(RestApiClientService.class);

    public RestApiClientService(RestClient.Builder builder,
                                ObjectMapper objectMapper,
                                RestApiUriProvider restApiUriProvider,
                                HttpSession session) {
        this.restClient = builder.build();
        this.objectMapper = objectMapper;
        this.restApiUriProvider = restApiUriProvider;
        this.session = session;
    }

    public void signUp(SignUpDto dto) {
        log.debug("Sending sign-up request for username {}.", dto.getUsername());
        restClient.post()
                .uri(restApiUriProvider.getSignUpUri())
                .contentType(APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toBodilessEntity();
        log.debug("Sign-up request successful for username {}.", dto.getUsername());
    }

    public SignInResponseDto signIn(SignInDto dto) {
        log.debug("Sending sign-in request for username {}.", dto.getUsername());
        SignInResponseDto response = restClient.post()
                .uri(restApiUriProvider.getSignInUri())
                .contentType(APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(SignInResponseDto.class);
        log.debug("Sign-in request successful for username {}.", dto.getUsername());
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public UserDto getUser() {
        log.debug("Sending request to get user info.");
        UserDto response = restClient.get()
                .uri(restApiUriProvider.getUserUri())
                .headers(headers -> {
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(UserDto.class);
        log.debug("User info get successfully.");
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public UserDto updateUser(UserDto user) {
        log.debug("Sending request to update user info for username {}.", user.getUsername());
        UserDto response = restClient.put()
                .uri(restApiUriProvider.getUpdateUserUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .body(user)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(UserDto.class);
        log.debug("User {} updated successfully.", user.getUsername());
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> getCurrentSubscriptions() {
        log.debug("Sending request to get user's subscriptions.");
        List<SubscriptionDto> response = restClient.get()
                .uri(restApiUriProvider.getCurrentSubscriptionsUri())
                .headers(headers -> {
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Successfully received user's subscriptions.");
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> getaAvailableSubscriptions() {
        log.debug("Sending request to get list of available subscriptions.");
        List<SubscriptionDto> response = restClient.get()
                .uri(restApiUriProvider.getAvailableSubscriptionsUri())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Successfully received list of all available subscriptions.");
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> addSubscription(String subscription) {
        log.debug("Sending request to add subscription {}.", subscription);
        List<SubscriptionDto> response = restClient.post()
                .uri(restApiUriProvider.getAddSubscriptionUri(subscription))
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Subscription {} add request successful.", subscription);
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> removeSubscription(String subscription) {
        log.debug("Sending request to add subscription {}.", subscription);
        List<SubscriptionDto> response = restClient.delete()
                .uri(restApiUriProvider.getRemoveSubscriptionUri(subscription))
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Subscription {} remove request successful.", subscription);
        return response;
    }

    public List<SubscriptionDto> getaAvailableQueries() {
        log.debug("Sending request to get list of available queries.");
        List<SubscriptionDto> response = restClient.get()
                .uri(restApiUriProvider.getAvailableSubscriptionsUri())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Successfully received list of all available queries.");
        return response;
    }

    public List<AnalyticsDto> getAnalyticsByQuery(String query) {
        log.debug("Sending request to get analytics for query {}.", query);
        List<AnalyticsDto> body = restClient.get()
                .uri(restApiUriProvider.getAnalyticsByQueryUri(), query, 30)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Request for analytics for query {} successful", query);
        return body;
    }

    public List<AnalyticsDto> getRecentAnalytics() {
        log.debug("Sending request to get recent analytics for all queries.");
        List<AnalyticsDto> body = restClient.get()
                .uri(restApiUriProvider.getRecentAnalyticsUri())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
        log.debug("Request for recent analytics for all queries successful");
        return body;
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) {
        List<String> errorMessages;
        HttpStatusCode statusCode;
        try (response) {
            statusCode = response.getStatusCode();
            errorMessages = parseErrorMessages(response);
        } catch (IOException e) {
            log.error("Error received from REST API, but failed to read response - " + e.getMessage(), e);
            throw new RestApiResponseReadException("Failed to read response body.", e);
        }
        if (statusCode.is5xxServerError()) {
            log.error("Server error received from REST API with status code - " + statusCode.value());
            throw new RestApiServerErrorException("Service unavailable. Received http status " + statusCode);
        }
        if (statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
            log.debug("Unauthorized status received from REST API.");
            try {
                log.debug("Trying to refresh tokens.");
                refreshTokens();
                log.debug("Tokens refreshed successfully");
            } catch (Exception e) {
                log.debug("Failed to refresh tokens");
                throw new RestApiUnauthorizedException("Unauthorized. Please, sign-in");
            }
            throw new RestApiTokenUpdatedException();
        }
        log.debug("Received client error from REST API with message " + errorMessages);
        throw new RestApiClientErrorException(errorMessages);
    }

    private List<String> parseErrorMessages(ClientHttpResponse response) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        ErrorResponseDto errorResponseDto = objectMapper.readValue(response.getBody(), ErrorResponseDto.class);
        if (errorResponseDto.getMessage() != null && !errorResponseDto.getMessage().isEmpty()) {
            errorMessages.add(errorResponseDto.getMessage());
        }
        if (errorResponseDto.getViolations() != null && !errorResponseDto.getViolations().isEmpty()) {
            List<String> collectedMessages = errorResponseDto.getViolations().stream()
                    .map(Violation::getMessage)
                    .filter(s -> s != null && !s.isEmpty())
                    .toList();
            errorMessages.addAll(collectedMessages);
        }
        if (errorMessages.isEmpty()) {
            errorMessages.add(DEFAULT_ERROR_MESSAGE);
        }
        return errorMessages;
    }

    private String getAccessToken() {
        return (String) this.session.getAttribute(ACCESS_TOKEN_NAME);
    }

    private String getRefreshToken() {
        return (String) this.session.getAttribute(REFRESH_TOKEN_NAME);
    }

    private void refreshTokens() throws ExecutionException, InterruptedException, TimeoutException {
        String refreshToken = getRefreshToken();
        Callable<SignInResponseDto> refreshTokenTask = () -> restClient.post()
                .uri(restApiUriProvider.getTokenRefreshUri())
                .contentType(APPLICATION_JSON)
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(refreshToken);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RestApiUnauthorizedException();
                })
                .body(SignInResponseDto.class);
        Future<SignInResponseDto> submitResult = tokenRefreshWorkersPool.submit(refreshTokenTask);
        SignInResponseDto newTokens = submitResult.get(tokensRefreshWaitTimeout, TimeUnit.SECONDS);
        session.setAttribute(ACCESS_TOKEN_NAME, newTokens.getAccessToken());
        session.setAttribute(REFRESH_TOKEN_NAME, newTokens.getRefreshToken());
    }
}
