package ru.borshchevskiy.webui.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.dto.error.ErrorResponseDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.exception.restapi.RestApiResponseReadException;
import ru.borshchevskiy.webui.exception.restapi.RestApiUnauthorizedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(RestApiClientService.class)
@ExtendWith(MockitoExtension.class)
class RestApiClientServiceTest {
    @Autowired
    private RestApiClientService restApiClientService;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RestApiUriProvider restApiUriProvider;
    @MockBean
    private HttpSession httpSession;

    private final String signUpUri = "https://app.test.com/signup";
    private final String signInUri = "https://app.test.com/signin";
    private final String getUserUri = "https://app.test.com/user";

    @Nested
    class TestSignUp {
        @Test
        @DisplayName("Test signup - receive ok status")
        public void testSignUp_success() throws JsonProcessingException {
            SignUpDto signUpDto = new SignUpDto();
            String username = "username";
            String password = "password";
            signUpDto.setUsername(username);
            signUpDto.setPassword(password);

            doReturn(signUpUri).when(restApiUriProvider).getSignUpUri();

            mockServer.expect(requestTo(signUpUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signUpDto)))
                    .andRespond(withSuccess());

            restApiClientService.signUp(signUpDto);
        }

        @Test
        @DisplayName("Test signup with empty username and get bad request status - RestApiException is thrown")
        public void testSignUp_withEmptyUsername_getBadRequest() throws JsonProcessingException {
            SignUpDto signUpDto = new SignUpDto();
            String username = "";
            String password = "password";
            signUpDto.setUsername(username);
            signUpDto.setPassword(password);

            doReturn(signUpUri).when(restApiUriProvider).getSignUpUri();

            mockServer.expect(requestTo(signUpUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signUpDto)))
                    .andRespond(withBadRequest().body(objectMapper.writeValueAsBytes("Bad request")));

            assertThrows(RestApiException.class, () -> restApiClientService.signUp(signUpDto));
        }

        @Test
        @DisplayName("Test signup with bad credentials and get unauthorized status - " +
                "RestApiUnauthorizedException is thrown")
        public void testSignUp_withBadCredentials_getUnauthorized() throws JsonProcessingException {
            SignUpDto signUpDto = new SignUpDto();
            String username = "username";
            String password = "password";
            signUpDto.setUsername(username);
            signUpDto.setPassword(password);
            ErrorResponseDto error = new ErrorResponseDto();
            error.setMessage("Unauthorized");

            doReturn(signUpUri).when(restApiUriProvider).getSignUpUri();

            mockServer.expect(requestTo(signUpUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signUpDto)))
                    .andRespond(withUnauthorizedRequest().body(objectMapper.writeValueAsBytes(error)));

            assertThrows(RestApiUnauthorizedException.class, () -> restApiClientService.signUp(signUpDto));
        }
    }

    @Nested
    class TestSignIn {
        @Test
        @DisplayName("Test signin, receive ok status and response entity - get required dto from response")
        public void testSignIn_success() throws JsonProcessingException {
            String username = "username";
            String password = "password";
            String accessToken = "accessToken";
            String refreshToken = "refreshToken";

            SignInDto signInDto = new SignInDto();
            signInDto.setUsername(username);
            signInDto.setPassword(password);

            SignInResponseDto signInResponseDto = new SignInResponseDto();
            signInResponseDto.setUsername(username);
            signInResponseDto.setAccessToken(accessToken);
            signInResponseDto.setRefreshToken(refreshToken);

            doReturn(signInUri).when(restApiUriProvider).getSignInUri();

            mockServer.expect(requestTo(signInUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signInDto)))
                    .andRespond(withSuccess()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper.writeValueAsBytes(signInResponseDto)));

            assertEquals(signInResponseDto, restApiClientService.signIn(signInDto));
        }

        @Test
        @DisplayName("Test signin with bad credentials, receive unauthorized status" +
                " - RestApiUnauthorizedException is thrown")
        public void testSignIn_withBadCredentials_getUnauthorized() throws JsonProcessingException {
            String username = "username";
            String password = "password";

            SignInDto signInDto = new SignInDto();
            signInDto.setUsername(username);
            signInDto.setPassword(password);

            ErrorResponseDto error = new ErrorResponseDto();
            error.setMessage("Unauthorized");

            doReturn(signInUri).when(restApiUriProvider).getSignInUri();

            mockServer.expect(requestTo(signInUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signInDto)))
                    .andRespond(withUnauthorizedRequest()
                            .body(objectMapper.writeValueAsBytes(error)));

            assertThrows(RestApiUnauthorizedException.class, () -> restApiClientService.signIn(signInDto));
        }
    }

    @Nested
    class TestUser {
        @Test
        @DisplayName("Test getuser, receive ok status and response entity - get required dto from response")
        public void testGetUser_success() throws JsonProcessingException {
            String username = "username";
            String accessToken = "accessToken";

            UserDto userDto = new UserDto();
            userDto.setUsername(username);

            doReturn(accessToken).when(httpSession).getAttribute(accessToken);
            doReturn(getUserUri).when(restApiUriProvider).getUserUri();

            mockServer.expect(requestTo(getUserUri))
                    .andExpect(header("AUTHORIZATION", "Bearer " + accessToken))
                    .andRespond(withSuccess()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper.writeValueAsBytes(userDto)));

            assertEquals(userDto, restApiClientService.getUser());
        }

        @Test
        @DisplayName("Test getuser with bad credentials, receive unauthorized - RestApiUnauthorizedException is thrown")
        public void testGetUser_getUnauthorized() throws JsonProcessingException {
            String accessToken = "accessToken";
            ErrorResponseDto error = new ErrorResponseDto();
            error.setMessage("Unauthorized");

            doReturn(accessToken).when(httpSession).getAttribute(accessToken);
            doReturn(getUserUri).when(restApiUriProvider).getUserUri();

            mockServer.expect(requestTo(getUserUri))
                    .andExpect(header("AUTHORIZATION", "Bearer " + accessToken))
                    .andRespond(withUnauthorizedRequest()
                            .body(objectMapper.writeValueAsBytes(error)));

            assertThrows(RestApiUnauthorizedException.class, () -> restApiClientService.getUser());
        }
    }

    @Nested
    class TestGeneralErrors {
        @Test
        @DisplayName("Test IO error, receive empty body - ResponseReadException is thrown")
        public void testSignIn_getEmptyBody() throws JsonProcessingException {
            String username = "username";
            String password = "password";

            SignInDto signInDto = new SignInDto();
            signInDto.setUsername(username);
            signInDto.setPassword(password);

            doReturn(signInUri).when(restApiUriProvider).getSignInUri();

            mockServer.expect(requestTo(signInUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signInDto)))
                    .andRespond(withBadRequest());

            assertThrows(RestApiResponseReadException.class, () -> restApiClientService.signIn(signInDto));
        }

        @Test
        @DisplayName("Test server error, receive 500 status - RestApiException is thrown")
        public void testSignIn_get500() throws JsonProcessingException {
            String username = "username";
            String password = "password";

            SignInDto signInDto = new SignInDto();
            signInDto.setUsername(username);
            signInDto.setPassword(password);

            doReturn(signInUri).when(restApiUriProvider).getSignInUri();

            mockServer.expect(requestTo(signInUri))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().bytes(objectMapper.writeValueAsBytes(signInDto)))
                    .andRespond(withServerError().body(objectMapper.writeValueAsBytes("Server error")));

            assertThrows(RestApiException.class, () -> restApiClientService.signIn(signInDto));
        }
    }
}