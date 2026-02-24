package com.org.back.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.org.back.dto.user.UserCreateDto;
import com.org.back.dto.user.UserResponseDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void testFindUserByEmail() throws EntityNotFoundException {
        // given
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userServiceImpl.findUserByEmail(email);

        // then
        assertNotNull(result);
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getFirstName(), result.firstName());
        assertEquals(user.getLastName(), result.lastName());
    }

    @Test
    void testFindUserByEmail_NotFound() throws EntityNotFoundException {
        // given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        UserResponseDto userDto = userServiceImpl.findUserByEmail(email);

        // then
        assertNull(userDto);
    }

    @Test
    void testGetUserById() throws EntityNotFoundException {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserResponseDto userDto = userServiceImpl.getUserById(userId);

        // then
        assertNotNull(userDto);
        assertEquals(user.getFirstName(), userDto.firstName());
        assertEquals(user.getLastName(), userDto.lastName());
    }

    @Test
    void testGetUserById_NotFound() throws EntityNotFoundException {
        // given
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
         UserResponseDto userDto = userServiceImpl.getUserById(userId);

        // then
        assertNull(userDto);
    }

    @Test
    void testAddUser() throws Exception {
        // given
        UserCreateDto userDto = new UserCreateDto(
                "John",
                "Doe",
                "john.doe@live.fr",
                "password123");

        User expectedUser = new User();
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Doe");
        expectedUser.setEmail("john.doe@live.fr");
        expectedUser.setPassword("encodedPassword");
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            User user = invocation.getArgument(1);
            user.setFirstName(userDto.firstName());
            user.setLastName(userDto.lastName());
            user.setEmail(userDto.email());
            return null;
        }).when(userMapper).createEntityFromDto(eq(userDto), any(User.class));
        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // when
        UserResponseDto savedUser = userServiceImpl.addUser(userDto);

        // then
        assertNotNull(savedUser);
        assertEquals(expectedUser.getFirstName(), savedUser.firstName());
        assertEquals(expectedUser.getLastName(), savedUser.lastName());
        assertEquals(expectedUser.getEmail(), savedUser.email());
        assertEquals(expectedUser.getPassword(), "encodedPassword");
    }

    @Test
    void testAddUser_EmailAlreadyExists() {
        // given
        UserCreateDto userDto = new UserCreateDto(
                "John",
                "Doe",
                "john.doe@live.fr",
                "password123");

        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(new User()));
        assertThrows(UserAlreadyExistsException.class, () -> {
            userServiceImpl.addUser(userDto);
        });
    }
}