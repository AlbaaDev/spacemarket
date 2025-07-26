package com.org.back;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    
    @InjectMocks
    private UserServiceImpl userServiceImpl;

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

    // @Test
    // public void testAddUser() {
    //     // given
    //     User userToSave = new User();
    //     userToSave.setEmail("john.doe@live.fr");
    //     userToSave.setFirstName("John");
    //     userToSave.setLastName("Doe");
    //     Mockito.when(userRepository.save(userToSave)).thenReturn(userToSave);

    //     // when
    //     User user = userServiceImpl.addUser(userToSave);

    //     // then
    //     assertNotNull(userToSave);;
    // }
}