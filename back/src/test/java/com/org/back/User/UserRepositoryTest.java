package com.org.back.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.org.back.models.User;
import com.org.back.repositories.UserRepository;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;


    @Test
    void givenNewUser_whenSave_thenSuccess() {

        User user = new User();
        user.setEmail("John@live.fr");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");
        User addedUser = userRepository.save(user);
        assertEquals(entityManager.find(User.class, addedUser.getId()), addedUser);
    }
}
