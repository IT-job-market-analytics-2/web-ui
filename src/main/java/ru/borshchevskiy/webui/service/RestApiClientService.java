package ru.borshchevskiy.webui.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.convert.Delimiter;
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
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.ResponseReadException;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class RestApiClientService {

    private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.";
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final RestApiUriProvider restApiUriProvider;

    public RestApiClientService(RestClient.Builder builder,
                                ObjectMapper objectMapper,
                                RestApiUriProvider restApiUriProvider) {
        this.restClient = builder.build();
        this.objectMapper = objectMapper;
        this.restApiUriProvider = restApiUriProvider;
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

    public UserDto getUser(String token) {
        UserDto response = restClient.get()
                .uri(restApiUriProvider.getUserUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(token);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .body(UserDto.class);
        return response;
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
}
