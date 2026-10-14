package com.org.back.Auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.org.back.controllers.AuthenticationController;
import com.org.back.mapper.UserMapper;
import com.org.back.security.config.SecurityConfiguration;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.AuthenticationService;
import com.org.back.services.UserServiceImpl;

// Production sets app.registration.enabled=false: nobody can create an account through the API.
@WebMvcTest(controllers = AuthenticationController.class, properties = "app.registration.enabled=false")
@Import(SecurityConfiguration.class)
class RegistrationClosedTest {

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
    @DisplayName("Registering is refused with 403 and creates no user when registration is closed")
    void register_is_forbidden() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Eve\",\"lastName\":\"Intruder\",\"email\":\"eve@test.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isForbidden());

        verify(userService, never()).addUser(any());
    }
}
