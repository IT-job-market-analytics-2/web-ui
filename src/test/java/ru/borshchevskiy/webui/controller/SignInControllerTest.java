package ru.borshchevskiy.webui.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.borshchevskiy.webui.dto.auth.SignInDto;
import ru.borshchevskiy.webui.dto.auth.SignInResponseDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.service.RestApiClientService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
class SignInControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestApiClientService restApiClientService;

    @Test
    @DisplayName("Get the sign-in page - requested view acquired")
    public void testGetSignUp() throws Exception {
        mockMvc.perform(get("/sign-in"))
                .andExpect(view().name("signin"));
    }

    @Test
    @DisplayName("Perform sign-in - receive redirect to root on success and session contains required attributes")
    public void testPerformSignUp_success() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        SignInDto signInDto = new SignInDto(testUsername, testPassword);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setUsername(testUsername);
        signInResponseDto.setAccessToken(accessToken);
        signInResponseDto.setRefreshToken(refreshToken);

        doReturn(signInResponseDto).when(restApiClientService).signIn(eq(signInDto));
        doThrow(new RestApiException()).when(restApiClientService).signIn(not(eq(signInDto)));

        HttpSession session = mockMvc.perform(post("/sign-in")
                        .param("username", testUsername)
                        .param("password", testPassword))
                .andExpect(redirectedUrl("/"))
                .andReturn()
                .getRequest()
                .getSession();

        assertEquals(accessToken, session.getAttribute(accessToken));
        assertEquals(refreshToken, session.getAttribute(refreshToken));
    }

    @Test
    @DisplayName("Perform sign-in with bad password - " +
            "get redirect to sign-in with flash attributes and no tokens saved")
    public void testPerformSignUp_withBadUsername_fail() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String badPassword = "badPassword";
        String badPasswordMessage = "Bad password message";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        List<String> badUsernameMessages = List.of(badPasswordMessage);

        SignInDto signInDto = new SignInDto(testUsername, testPassword);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setUsername(testUsername);
        signInResponseDto.setAccessToken(accessToken);
        signInResponseDto.setRefreshToken(refreshToken);

        SignInDto redirectDto = new SignInDto();
        redirectDto.setUsername(testUsername);

        doReturn(signInResponseDto).when(restApiClientService).signIn(eq(signInDto));
        doThrow(new RestApiException(badUsernameMessages)).when(restApiClientService).signIn(not(eq(signInDto)));

        HttpSession session = mockMvc.perform(post("/sign-in")
                        .param("username", testUsername)
                        .param("password", badPassword))
                .andExpectAll(
                        redirectedUrl("/sign-in"),
                        flash().attribute("user", redirectDto),
                        flash().attribute("errorMessages", badUsernameMessages)
                )
                .andReturn()
                .getRequest()
                .getSession();

        assertNull(session.getAttribute(accessToken));
        assertNull(session.getAttribute(refreshToken));
    }
}