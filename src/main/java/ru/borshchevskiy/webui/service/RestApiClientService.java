package ru.borshchevskiy.webui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.dto.error.ErrorResponseDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.ResponseReadException;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class RestApiClientService {

    private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.";
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final RestApiUriProvider restApiUriProvider;

    public RestApiClientService(RestClient restClient,
                                ObjectMapper objectMapper,
                                RestApiUriProvider restApiUriProvider) {
        this.restClient = restClient;
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
        ResponseEntity<SignInResponseDto> response = restClient.post()
                .uri(restApiUriProvider.getSignInUri())
                .contentType(APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toEntity(SignInResponseDto.class);
        return response.getBody();
    }

    public UserDto getUser(String token) {
        ResponseEntity<UserDto> response = restClient.get()
                .uri(restApiUriProvider.getUserUri())
                .headers(headers -> {
                    headers.setContentType(APPLICATION_JSON);
                    headers.setBearerAuth(token);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toEntity(UserDto.class);
        return response.getBody();
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) {
        String errorMessage;
        HttpStatusCode statusCode;
        try (response; InputStream body = response.getBody()) {
            ErrorResponseDto errorResponseDto = objectMapper.readValue(body, ErrorResponseDto.class);
            errorMessage = errorResponseDto.getMessage().isBlank() ? DEFAULT_ERROR_MESSAGE
                    : errorResponseDto.getMessage();
            statusCode = response.getStatusCode();
        } catch (IOException e) {
            throw new ResponseReadException("Failed to read response body.", e);
        }
        if (statusCode.is5xxServerError()) {
            throw new RestApiException("Service unavailable. Received http status " + statusCode);
        }
        if (statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
            throw new RestApiUnauthorizedException(errorMessage);
        }
        throw new RestApiException(errorMessage);
    }
}
