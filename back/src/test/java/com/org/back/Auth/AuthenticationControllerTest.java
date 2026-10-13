package com.org.back.Auth;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.org.back.controllers.AuthenticationController;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.security.config.SecurityConfiguration;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.AuthenticationService;
import com.org.back.services.UserServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfiguration.class)
class AuthenticationControllerTest {

    private static final long ONE_DAY_IN_MS = 86_400_000L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private UserServiceImpl userService;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private AuthenticationProvider authenticationProvider;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("The jwt cookie lives exactly as long as the token inside it")
    void login_cookie_expires_with_the_token() throws Exception {
        when(authenticationService.authenticate(any())).thenReturn(new User());
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.getExpirationTime()).thenReturn(ONE_DAY_IN_MS);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"john@work.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=86400")));
    }

    @Test
    @DisplayName("A request without a valid session is answered 401, so the front can send the user to the login page")
    void request_without_session_is_unauthorized() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("An expired or tampered token is answered 401, like no token at all")
    void request_with_unreadable_token_is_unauthorized() throws Exception {
        when(jwtService.extractUsername("expired-token")).thenThrow(new ExpiredJwtException(null, null, "JWT expired"));

        mockMvc.perform(get("/auth/me").cookie(new Cookie("jwt", "expired-token")))
                .andExpect(status().isUnauthorized());
    }
}
