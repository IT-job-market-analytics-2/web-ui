package ru.borshchevskiy.webui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.dto.error.ErrorResponseDto;
import ru.borshchevskiy.webui.dto.error.Violation;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.*;

import java.io.IOException;
import java.io.InputStream;
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
        restClient.post()
                .uri(restApiUriProvider.getSignUpUri())
                .contentType(APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toBodilessEntity();
    }

    public SignInResponseDto signIn(SignInDto dto) {
        return restClient.post()
                .uri(restApiUriProvider.getSignInUri())
                .contentType(APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(SignInResponseDto.class);
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public UserDto getUser() {
        UserDto response = restClient.get()
                .uri(restApiUriProvider.getUserUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(UserDto.class);
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public UserDto updateUser(UserDto user) {
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
        return response;
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> getCurrentSubscriptions() {
        return restClient.get()
                .uri(restApiUriProvider.getCurrentSubscriptionsUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> getaAvailableSubscriptions() {
        return restClient.get()
                .uri(restApiUriProvider.getAvailableSubscriptionsUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> addSubscription(String subscription) {
        return restClient.post()
                .uri(restApiUriProvider.getAddSubscriptionUri(subscription))
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Retryable(retryFor = RestApiTokenUpdatedException.class, maxAttempts = 2)
    public List<SubscriptionDto> removeSubscription(String subscription) {
        return restClient.delete()
                .uri(restApiUriProvider.getRemoveSubscriptionUri(subscription))
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getAccessToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) {
        List<String> errorMessages = new ArrayList<>();
        HttpStatusCode statusCode;
        try (response; InputStream body = response.getBody()) {
            statusCode = response.getStatusCode();
            ErrorResponseDto errorResponseDto = objectMapper.readValue(body, ErrorResponseDto.class);
            errorMessages.addAll(parseErrorMessages(errorResponseDto));
            if (errorMessages.isEmpty()) {
                errorMessages.add(DEFAULT_ERROR_MESSAGE);
            }
        } catch (IOException e) {
            throw new RestApiResponseReadException("Failed to read response body.", e);
        }
        if (statusCode.is5xxServerError()) {
            throw new RestApiServerErrorException("Service unavailable. Received http status " + statusCode);
        }
        if (statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
            try {
                refreshTokens();
            } catch (Exception e) {
                throw new RestApiUnauthorizedException("Unauthorized. Please, sign-in");
            }
            throw new RestApiTokenUpdatedException();
        }
        throw new RestApiClientErrorException(errorMessages);
    }

    private List<String> parseErrorMessages(ErrorResponseDto errorResponseDto) {
        List<String> errorMessages = new ArrayList<>();
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
