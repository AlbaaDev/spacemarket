package com.org.back;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.back.controllers.UserController;
import com.org.back.dto.User.UserUpdateProfileDto;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.UserServiceImpl;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
class UserControllerTest {
    
   @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @WithMockUser
    void allUsers_should_return_all_users() throws Exception {
        // GIVEN
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        List<User> userList = List.of(user1, user2);
        when(userService.getAllUsers()).thenReturn(userList);

        // WHEN
        ResultActions response = mockMvc.perform(get("/users/").accept(MediaType.APPLICATION_JSON));

        // THEN
        response
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    @DisplayName("Should successfully update user profile  when valid data is provided")
    void updateProfile_should_update_user_profile() throws Exception { 
        // GIVEN
        UserUpdateProfileDto updateProfileDto = new UserUpdateProfileDto("abi", "faz");

        User authUser = new User();
        authUser.setId(1L);
        authUser.setFirstName("abi");
        authUser.setLastName("faz");
        doNothing().when(userService).updateUserProfile(any(User.class), eq(updateProfileDto) );

        // WHEN
        ResultActions response =  mockMvc.perform(put("/users/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(updateProfileDto)));

        // THEN
        response.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 403 when user tries to update profile without CSRF token")
    void updateProfile_should_return_403_when_no_csrf_token() throws Exception {
        // GIVEN
        UserUpdateProfileDto updateProfileDto = new UserUpdateProfileDto("abi", "faz");
        doNothing().when(userService).updateUserProfile(any(User.class), eq(updateProfileDto) );

        // WHEN
        ResultActions response =  mockMvc.perform(put("/users/me/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProfileDto)));

        // THEN
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when user tries to update profile with invalid profile data ")
    void updateProfile_should_return_400_when_invalid_data_is_provided() throws Exception {
        // GIVEN
        UserUpdateProfileDto updateProfileDto = new UserUpdateProfileDto("", "");
        doNothing().when(userService).updateUserProfile(any(User.class), eq(updateProfileDto) );

        // WHEN
        ResultActions response =  mockMvc.perform(put("/users/me/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(objectMapper.writeValueAsString(updateProfileDto)));

        // THEN
        response.andExpect(status().isBadRequest());
        // Vérifiez que le service n'est jamais appelé
        verify(userService, never()).updateUserProfile(any(), any());
    }
}
