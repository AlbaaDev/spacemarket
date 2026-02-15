package com.org.back.User;


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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.back.controllers.UserController;
import com.org.back.dto.user.UserUpdateProfileDto;
import com.org.back.dto.user.UserUpdateSettingsDto;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.UserServiceImpl;

@WebMvcTest(UserController.class)
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
    @DisplayName("Should return 200 when user tries to update profile with valid data")
    void updateProfile_should_return_200_when_profile_data_is_valid_and_user_is_authenticated() throws Exception { 
        // GIVEN
        UserUpdateProfileDto updateProfileDto = new UserUpdateProfileDto("abi", "faz");
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
        verify(userService, never()).updateUserProfile(any(), any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should return 401 when user tries to update profile without authentication")
    void updateProfile_should_return_401_when_user_not_authenticated() throws Exception {
        // GIVEN
        UserUpdateProfileDto updateProfileDto = new UserUpdateProfileDto("abi", "faz");
        doNothing().when(userService).updateUserProfile((any(User.class)), eq(updateProfileDto));
      
        // WHEN
        ResultActions response =  mockMvc.perform(put("/users/me/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())
        .content(objectMapper.writeValueAsString(updateProfileDto)));


        // THEN
        response.andExpect(status().isUnauthorized());
        verify(userService, never()).updateUserProfile(null, updateProfileDto);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 200 when user tries to update settings with valid data")
    void updateSettings_should_return_200_when_settings_data_is_valid() throws Exception { 
        // GIVEN
        UserUpdateSettingsDto updateSettingsDto = new UserUpdateSettingsDto("test4@live.fr");
        doNothing().when(userService).updateUserSettings(any(User.class), eq(updateSettingsDto));

        // WHEN
        ResultActions response = mockMvc.perform(put("/users/me/settings")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(objectMapper.writeValueAsString(updateSettingsDto))
        );

        // THEN
        response.andExpect(status().isOk());
    }


    @Test
    @WithMockUser
    @DisplayName("Should return 400 when user tries to update settings with invalid data")
    void updateSettings_should_return_400_when_invalid_data_is_provided() throws Exception {
        // GIVEN
        UserUpdateSettingsDto updateSettingsDto = new UserUpdateSettingsDto("");
        doNothing().when(userService).updateUserSettings(any(User.class), eq(updateSettingsDto));

        // WHEN
        ResultActions response = mockMvc.perform(put("/users/me/settings")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(objectMapper.writeValueAsString(updateSettingsDto))
        );

        // THEN
        response.andExpect(status().isBadRequest());
        verify(userService, never()).updateUserSettings(any(), any());
    }
}
