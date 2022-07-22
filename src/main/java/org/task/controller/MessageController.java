package org.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.task.entity.api.HistoryMessage;
import org.task.entity.api.Message;
import org.task.entity.db.UserName;
import org.task.repository.MessageRepository;
import org.task.repository.UserNameRepository;
import org.task.security.JwtTokenUtil;
import org.task.utils.ParserUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MessageController {

    @Autowired
    UserNameRepository userNameRepository;

    @Autowired
    MessageRepository messageRepository;

    @PostMapping(path = "/message", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<HistoryMessage> post(@RequestHeader("Authorization") String token, @RequestBody Message message) {
        String name = JwtTokenUtil.getNameByToken(ParserUtils.parseTokenFromHeader(token));
        return workWithMessages(message, getAndCheckUserName(name));
    }

    private UserName getAndCheckUserName(String name) {
        return userNameRepository.findByName(
                        name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
                );
    }

    private List<HistoryMessage> workWithMessages(Message message, UserName userName) {
        validateUserName(userName, message);
        String name = userName.getName();
        if (ParserUtils.isNeedHistory(message.getMessage())) {
            long count = ParserUtils.getCountHistory(message.getMessage());
            return messageRepository.findAllByIdUserNameWithLimit(userName.getId(), count).stream()
                    .map(message1 -> new HistoryMessage(name, message1.getMessage(), message1.getDateTime()))
                    .collect(Collectors.toList());
        }
        messageRepository.save(new org.task.entity.db.Message(userName.getId(), message.getMessage()));
        return Collections.singletonList(new HistoryMessage(name, message.getMessage(), Calendar.getInstance()));
    }

    private void validateUserName(UserName userName, Message message) {
        if (!userName.getName().equals(message.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field name in body request and sender are different");
        }
    }
}
