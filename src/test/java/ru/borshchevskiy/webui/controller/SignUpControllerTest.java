package ru.borshchevskiy.webui.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.borshchevskiy.webui.dto.auth.SignUpDto;
import ru.borshchevskiy.webui.dto.user.UserDto;
import ru.borshchevskiy.webui.exception.restapi.RestApiException;
import ru.borshchevskiy.webui.service.RestApiClientService;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestApiClientService restApiClientService;

    @Test
    @DisplayName("Get the sign-up page - requested view acquired")
    public void testGetSignUp() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(view().name("signup"));
    }

    @Test
    @DisplayName("Perform sign-up - receive redirect to sign-in on success")
    public void testPerformSignUp_success() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        SignUpDto signUpDto = new SignUpDto(testUsername, testPassword);
        doNothing().when(restApiClientService).signUp(eq(signUpDto));
        doThrow(new RestApiException()).when(restApiClientService).signUp(not(eq(signUpDto)));

        mockMvc.perform(post("/sign-up")
                        .param("username", testUsername)
                        .param("password", testPassword))
                .andExpect(redirectedUrl("/sign-in"));
    }

    @Test
    @DisplayName("Perform sign-up with bad username - get redirect to sign-up with flash attributes")
    public void testPerformSignUp_withBadUsername_fail() throws Exception {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        SignUpDto signUpDto = new SignUpDto(testUsername, testPassword);

        String badUsername = "badUsername";
        String badUsernameMessage = "Bad username message";

        UserDto redirectUserDto = new UserDto();
        redirectUserDto.setUsername(badUsername);

        doNothing().when(restApiClientService).signUp(eq(signUpDto));
        doThrow(new RestApiException(badUsernameMessage)).when(restApiClientService).signUp(not(eq(signUpDto)));

        mockMvc.perform(post("/sign-up")
                        .param("username", badUsername)
                        .param("password", testPassword))
                .andExpectAll(
                        redirectedUrl("/sign-up"),
                        flash().attribute("user", redirectUserDto),
                        flash().attribute("errorMessage", badUsernameMessage)
                );
    }
}