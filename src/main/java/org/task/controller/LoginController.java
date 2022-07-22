package org.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.task.entity.api.Login;
import org.task.entity.api.Token;
import org.task.entity.db.UserName;
import org.task.repository.UserNameRepository;
import org.task.repository.UserPasswordRepository;
import org.task.security.JwtTokenUtil;

@RestController
public class LoginController {
    @Autowired
    UserPasswordRepository userPasswordRepository;
    @Autowired
    UserNameRepository userNameRepository;

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Token post(@RequestBody Login login) {
        UserName userName = userNameRepository.findByName(
                        login.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password")
                );
        userPasswordRepository.findByIdUserNameAndPasswd(
                        userName.getId(), login.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password")
                );
        return new Token(JwtTokenUtil.generateTokenByName(login.getName()));
    }
}
