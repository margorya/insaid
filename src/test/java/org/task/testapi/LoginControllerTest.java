package org.task.testapi;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.task.Application;
import org.task.controller.LoginController;
import org.task.entity.api.Login;
import org.task.entity.api.TokenTestFor;
import org.task.repository.UserNameRepository;
import org.task.repository.UserPasswordRepository;
import org.task.security.JwtTokenUtil;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureEmbeddedDatabase
public class LoginControllerTest {

    @Autowired
    LoginController loginController;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    UserNameRepository userNameRepository;

    @Autowired
    UserPasswordRepository userPasswordRepository;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(loginController);
    }

    @Test
    public void testResponseToken() {
        TokenTestFor tokenTestFor = restTemplate.postForObject("http://localhost:" + port + "/login",
                new Login("Vera", "pass"), TokenTestFor.class);
        Assertions.assertNotNull(tokenTestFor);
        Assertions.assertTrue(StringUtils.isNotEmpty(tokenTestFor.getToken()));
    }

    @Test
    public void testNegativeToken() {
        List<Login> loginList = new ArrayList<>();
        loginList.add(new Login("Test", "test"));
        loginList.add(new Login("Vera", "test"));
        loginList.add(new Login("Test", "pass"));
        for (Login login : loginList) {
            try {
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/login",
                        login,
                        String.class
                );
            } catch (ResourceAccessException e) {
                Assertions.assertEquals(((HttpRetryException) e.getCause()).responseCode(), HttpStatus.UNAUTHORIZED.value());
            }
        }
    }

    @Test
    public void testTokenEqualsName() {
        TokenTestFor token = this.restTemplate.postForObject("http://localhost:" + port + "/login",
                new Login("Vera", "pass"), TokenTestFor.class);
        String name = JwtTokenUtil.getNameByToken(token.getToken());
        Assertions.assertEquals(name, "Vera");
    }

}
