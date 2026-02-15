package com.org.back.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.org.back.dto.user.UserCreateDto;
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
    void testFindUserByEmail() {
        // given
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User result = userServiceImpl.findUserByEmail(email).get();

        // then
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    void testFindUserByEmail_NotFound() {
        // given
        String email = "nonexistent@example.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        Boolean result = userServiceImpl.findUserByEmail(email).isPresent();

        // then
        assertFalse(result);
    }

    @Test
    void testGetUserById() {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        User result = userServiceImpl.getUserById(userId).get();

        // then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    void testGetUserById_NotFound() {
        // given
        Long userId = 99L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        Boolean result = userServiceImpl.getUserById(userId).isPresent();

        // then
        assertFalse(result);
    }

    @Test
    void testAddUser() throws Exception {
    // given
    UserCreateDto userDto = new UserCreateDto(
        "John", 
        "Doe", 
        "john.doe@live.fr",
        "password123"
        );
        
    User expectedUser = new User();
    expectedUser.setFirstName("John");
    expectedUser.setLastName("Doe");
    expectedUser.setEmail("john.doe@live.fr");
    expectedUser.setPassword("encodedPassword");
    when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());
    Mockito.doAnswer(invocation -> {
        User user = invocation.getArgument(1);
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        return null;
    }).when(userMapper).createEntityFromDto(Mockito.eq(userDto), Mockito.any(User.class));
    Mockito.when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

    // when
    User savedUser = userServiceImpl.addUser(userDto);

    // then
    assertNotNull(savedUser);
    assertEquals(expectedUser.getFirstName(), savedUser.getFirstName());
    assertEquals(expectedUser.getLastName(), savedUser.getLastName());
    assertEquals(expectedUser.getEmail(), savedUser.getEmail());
    assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void  testAddUser_EmailAlreadyExists() {
        // given
        UserCreateDto userDto = new UserCreateDto(
            "John", 
            "Doe", 
            "john.doe@live.fr",
            "password123"
        );

        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(new User()));
        assertThrows(UserAlreadyExistsException.class, () -> {
            userServiceImpl.addUser(userDto);
        });
    }
}   