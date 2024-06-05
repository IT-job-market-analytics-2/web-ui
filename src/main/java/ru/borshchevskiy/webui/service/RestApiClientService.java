package ru.borshchevskiy.webui.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.dto.error.ErrorResponseDto;
import ru.borshchevskiy.webui.dto.error.Violation;
import ru.borshchevskiy.webui.dto.subscription.SubscriptionDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.ResponseReadException;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiRequestBuildException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class RestApiClientService {

    private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.";
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

    public UserDto getUser() {
        UserDto response = restClient.get()
                .uri(restApiUriProvider.getUserUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(UserDto.class);
        return response;
    }

    public UserDto updateUser(UserDto user) {
        try {
            UserDto response = restClient.put()
                    .uri(restApiUriProvider.getUpdateUserUri())
                    .headers(headers -> {
                        headers.setContentType(APPLICATION_JSON);
                        headers.setBearerAuth(getToken());
                    })
                    .body(objectMapper.writeValueAsString(user))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, this::handleError)
                    .body(UserDto.class);
            return response;
        } catch (JsonProcessingException e) {
            throw new RestApiRequestBuildException("Failed to build REST API request with body " + user.toString(), e);
        }
    }

    public List<SubscriptionDto> getCurrentSubscriptions() {
        return restClient.get()
                .uri(restApiUriProvider.getCurrentSubscriptionsUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getToken());
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<SubscriptionDto> getaAvailableSubscriptions() {
        return restClient.get()
                .uri(restApiUriProvider.getAvailableSubscriptionsUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(getToken());
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
            throw new ResponseReadException("Failed to read response body.", e);
        }
        if (statusCode.is5xxServerError()) {
            throw new RestApiException("Service unavailable. Received http status " + statusCode);
        }
        if (statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
            throw new RestApiUnauthorizedException(errorMessages);
        }
        throw new RestApiException(errorMessages);
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

    private String getToken() {
        return (String) this.session.getAttribute("accessToken");
    }
}
