package com.flower.ourdiary.unittest.repository;

import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("local")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void insertUser() {
        User user = new User();

        int insertCount = userRepository.insertUser(user);

        assertEquals(insertCount, 1);
        assertNotNull(user.getSeq());
        assertTrue(user.getSeq() > 0);
    }

    @Test
    void findUserBySeq() {
        User user = new User();

        userRepository.insertUser(user);

        User foundUser = userRepository.findUserBySeq(user.getSeq());

        assertNotNull(foundUser);
        assertNotNull(foundUser.getSeq());
        assertEquals(user.getSeq(), foundUser.getSeq());
    }
}
