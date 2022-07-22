package org.task.testapi;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.task.Application;
import org.task.controller.MessageController;
import org.task.entity.api.Message;
import org.task.entity.api.Token;
import org.task.entity.db.UserName;
import org.task.repository.MessageRepository;
import org.task.repository.UserNameRepository;
import org.task.security.JwtTokenUtil;

import java.net.HttpRetryException;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureEmbeddedDatabase
public class MessageControllerTest {
    private static final UserName USER_NAME = new UserName(1, "Vera");
    @Autowired
    MessageController messageController;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    UserNameRepository userNameRepository;

    @Autowired
    MessageRepository messageRepository;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(messageController);
    }

    @Test
    public void testResponseMessageSimpleRequest() {
        Token token = new Token(JwtTokenUtil.generateTokenByName(USER_NAME.getName()));
        Message message = new Message(USER_NAME.getName(), "Hi Test!");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer_" + token.getToken());
        HttpEntity<Message> httpEntity = new HttpEntity<>(message, headers);
        List<LinkedHashMap> historyMessageTests = (List<LinkedHashMap>) restTemplate.postForObject(
                "http://localhost:" + port + "/message",
                httpEntity, List.class
        );
        Assertions.assertTrue(CollectionUtils.isNotEmpty(historyMessageTests));
        Map<String, String> historyMessage = (LinkedHashMap<String, String>) historyMessageTests.get(0);
        Assertions.assertTrue(StringUtils.isNotEmpty(historyMessage.get("message"))
                && historyMessage.get("message").equals("Hi Test!"));
        Assertions.assertTrue(StringUtils.isNotEmpty(historyMessage.get("name"))
                && historyMessage.get("name").equals(USER_NAME.getName()));
        List<org.task.entity.db.Message> messages = messageRepository.findAllByIdUserNameWithLimit(USER_NAME.getId(), 1L);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(messages));
        Assertions.assertEquals(message.getMessage(), messages.get(0).getMessage());
        Assertions.assertTrue(Calendar.getInstance().after(messages.get(0).getDateTime()));
    }

    @Test
    public void testResponseMessageHistoryRequest() {
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
        httpEntity = new HttpEntity<>(new Message(USER_NAME.getName(), "history 5"), headers);
        List<LinkedHashMap> historyMessages = (List<LinkedHashMap>) restTemplate.postForObject("http://localhost:" + port + "/message",
                httpEntity, List.class);

        Assertions.assertTrue(CollectionUtils.isNotEmpty(historyMessages));
        Assertions.assertEquals(messageListForRequest.size(), historyMessages.size());
        int i = messageListForRequest.size() - 1;
        for (Map<String, String> historyMessage : historyMessages) {
            Assertions.assertTrue(StringUtils.isNotEmpty(historyMessage.get("message"))
                    && historyMessage.get("message").equals(messageListForRequest.get(i).getMessage()));
            Assertions.assertTrue(StringUtils.isNotEmpty(historyMessage.get("name"))
                    && historyMessage.get("name").equals(USER_NAME.getName()));
            i--;
        }
    }

    @Test
    public void testResponseMessageNegativeNameInMsg() {
        Token token = new Token(JwtTokenUtil.generateTokenByName(USER_NAME.getName()));
        Message message = new Message("Nina", "Hi Test!");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer_" + token.getToken());
        HttpEntity<Message> httpEntity = new HttpEntity<>(message, headers);
        try {
            restTemplate.postForObject(
                    "http://localhost:" + port + "/message",
                    httpEntity, String.class);
        } catch (ResourceAccessException e) {
            Assertions.assertEquals(((HttpRetryException)e.getCause()).responseCode(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @Test
    public void testResponseMessageNegativeTokenIsIncorrect() {
        Token token = new Token(JwtTokenUtil.generateTokenByName("123"));
        Message message = new Message(USER_NAME.getName(), "Hi Test!");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer_" + token.getToken());
        HttpEntity<Message> httpEntity = new HttpEntity<>(message, headers);
        try {
            restTemplate.postForObject(
                    "http://localhost:" + port + "/message",
                    httpEntity, String.class);
        } catch (ResourceAccessException e) {
            Assertions.assertEquals(((HttpRetryException)e.getCause()).responseCode(), HttpStatus.UNAUTHORIZED.value());
        }
    }

}
