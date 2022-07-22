package org.task.testdb;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.task.Application;
import org.task.entity.api.Message;
import org.task.entity.api.Token;
import org.task.entity.db.UserName;
import org.task.repository.MessageRepository;
import org.task.security.JwtTokenUtil;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureEmbeddedDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class MessageRepositoryTest {
    private static final UserName USER_NAME = new UserName(1, "Vera");
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    MessageRepository messageRepository;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(messageRepository);
    }

    @Test
    public void findAllByIdUserNameWithLimitTest() {
        Token token = new Token(JwtTokenUtil.generateTokenByName("Vera"));
        List<Message> messageListForRequest = new ArrayList<>();
        messageListForRequest.add(new Message(USER_NAME.getName(), "Hi Test!"));
        messageListForRequest.add(new Message(USER_NAME.getName(), "My name is Vera"));
        messageListForRequest.add(new Message(USER_NAME.getName(), "Have you good time!"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer_" + token.getToken());
        HttpEntity<Message> httpEntity;

        for (Message message : messageListForRequest) {
            httpEntity = new HttpEntity<>(message, headers);
            restTemplate.postForObject("http://localhost:" + port + "/message",
                    httpEntity, List.class);
        }
        long limit = 2;
        List<org.task.entity.db.Message> resultSizeTwo = messageRepository.findAllByIdUserNameWithLimit(USER_NAME.getId(), limit);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(resultSizeTwo));
        Assertions.assertEquals(limit, resultSizeTwo.size());
        limit = messageListForRequest.size();
        List<org.task.entity.db.Message> resultSizeMsg = messageRepository.findAllByIdUserNameWithLimit(USER_NAME.getId(), limit);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(resultSizeMsg));
        Assertions.assertEquals(limit, resultSizeMsg.size());
    }
}
