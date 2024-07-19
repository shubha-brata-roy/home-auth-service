package com.royhome.homeauthservice.controllers;

import com.royhome.homeauthservice.dtos.LoginDto;
import com.royhome.homeauthservice.dtos.SignUpDto;
import com.royhome.homeauthservice.models.User;
import com.royhome.homeauthservice.services.UserService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/sign-up", method = RequestMethod.POST)
    public ResponseEntity<String> signUp(@RequestParam(name = "name") String name,
                                         @RequestParam(name = "email") String email,
                                         @RequestParam(name = "password") String password,
                                         @RequestParam(name = "phone") String phone) {

        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setEmail(email);
        signUpDto.setName(name);
        signUpDto.setPassword(password);
        signUpDto.setPhone(phone);

        try {
            return new ResponseEntity<>(userService.signUp(signUpDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestParam(name = "email") String email,
                                          @RequestParam(name = "password") String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        try {
            Pair<User, MultiValueMap<String, String>> userWithHeaders = userService.login(loginDto);
            return new ResponseEntity<>("Login Successful", userWithHeaders.b, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout(@RequestParam("Authentication-Info") String token) {
        try {
            return new ResponseEntity<>(userService.logout(token), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
