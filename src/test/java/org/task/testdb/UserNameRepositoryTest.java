package org.task.testdb;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.task.Application;
import org.task.entity.db.UserName;
import org.task.repository.UserNameRepository;

import java.util.Optional;

@AutoConfigureEmbeddedDatabase
@SpringBootTest(classes = Application.class)
public class UserNameRepositoryTest {
    private static final UserName USER_NAME = new UserName(1, "Vera");

    @Autowired
    UserNameRepository userNameRepository;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(userNameRepository);
    }

    @Test
    public void findByNameTest() {
        Optional<UserName> userName = userNameRepository.findByName(USER_NAME.getName());
        Assertions.assertTrue(userName.isPresent());
    }

    @Test
    public void findByNameNegateTest() {
        Optional<UserName> userName = userNameRepository.findByName("123");
        Assertions.assertFalse(userName.isPresent());
    }
}
