package org.task.testdb;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.task.Application;
import org.task.entity.db.UserPassword;
import org.task.repository.UserPasswordRepository;

import java.util.Optional;

@AutoConfigureEmbeddedDatabase
@SpringBootTest(classes = Application.class)
public class UserPasswordRepositoryTest {
    private static final int VALID_ID = 1;
    private static final String VALID_PASSWD = "pass";
    @Autowired
    UserPasswordRepository userPasswordRepository;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(userPasswordRepository);
    }

    @Test
    public void findByIdUserNameAndPasswdTest() {
        Optional<UserPassword> userPassword = userPasswordRepository.findByIdUserNameAndPasswd(VALID_ID, VALID_PASSWD);
        Assertions.assertTrue(userPassword.isPresent());
    }

    @Test
    public void findByIdUserNameAndPasswdNegateTest() {
        Optional<UserPassword> failId = userPasswordRepository.findByIdUserNameAndPasswd(123, VALID_PASSWD);
        Assertions.assertFalse(failId.isPresent());

        Optional<UserPassword> failPass = userPasswordRepository.findByIdUserNameAndPasswd(VALID_ID, "pass1");
        Assertions.assertFalse(failPass.isPresent());
    }
}
