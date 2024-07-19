package com.royhome.homeauthservice.services;

import com.nimbusds.jose.JOSEException;
import com.royhome.homeauthservice.dtos.JWTDto;
import com.royhome.homeauthservice.dtos.LoginDto;
import com.royhome.homeauthservice.dtos.SignUpDto;
import com.royhome.homeauthservice.exceptions.IncorrectPasswordException;
import com.royhome.homeauthservice.exceptions.InvalidEmailException;
import com.royhome.homeauthservice.exceptions.InvalidOrExpiredTokenException;
import com.royhome.homeauthservice.exceptions.UserNotFoundException;
import com.royhome.homeauthservice.models.Role;
import com.royhome.homeauthservice.models.Session;
import com.royhome.homeauthservice.models.SessionStatus;
import com.royhome.homeauthservice.models.User;
import com.royhome.homeauthservice.repositories.SessionRepository;
import com.royhome.homeauthservice.repositories.UserRepository;
import com.royhome.homeauthservice.security.JWTDecoder;
import com.royhome.homeauthservice.security.JWTGenerator;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTGenerator jwtGenerator;
    private final JWTDecoder jwtDecoder;

    @Autowired
    public UserService(UserRepository userRepository, SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       @Qualifier("JWTGeneratorWithJJWT") JWTGenerator jwtGenerator,
                       @Qualifier("JWTDecoderWithJJWT") JWTDecoder jwtDecoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.jwtDecoder = jwtDecoder;
    }

    public String signUp(SignUpDto signUpDto) throws InvalidEmailException {

        //validate the email syntax
        if(!isValidEmail(signUpDto.getEmail())) {
            throw new InvalidEmailException("Invalid email syntax. Please enter a valid email address.");
        }

        Optional<User> userOptional = userRepository.findUserByEmail(signUpDto.getEmail());
        if(userOptional.isPresent()) {
            return("User with email: "+signUpDto.getEmail()+" already exists.");
        }

        User user = new User();

        user.setEmail(signUpDto.getEmail());
        user.setName(signUpDto.getName());
        user.setHashPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword()));
        user.setPhoneNumber(signUpDto.getPhone());

        // After a user is created, the Admin(s) will receive an email to assign a role to the user
        // Until then the user will have NO roles assigned, and they cannot do anything after they log in
        user.setRoles(List.of(Role.ADMIN));
        user = userRepository.save(user);
        return("User created successfully. Please wait for the role assignment email.");
    }

    public Pair<User,MultiValueMap<String,String>> login(LoginDto loginDto) throws UserNotFoundException, IncorrectPasswordException, JOSEException {
        Optional<User> userOptional = userRepository.findUserByEmail(loginDto.getEmail());
        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email: "+loginDto.getEmail()+" not found.");
        }
        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getHashPassword())) {
            throw new IncorrectPasswordException("Password is incorrect.");
        }

        Session session = new Session();
        List<String> roles = new ArrayList<>();

        for(Role role : user.getRoles()) {
            roles.add(role.name());
        }

        JWTDto jwtDto = jwtGenerator.generate(user.getEmail(), user.getName(), roles);
        session.setToken(jwtDto.getToken());
        session.setExpiryAt(jwtDto.getExpiryAt());
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);

        sessionRepository.save(session);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, jwtDto.getToken());

        return new Pair<User,MultiValueMap<String,String>>(user,headers);
    }

    public String logout(String token) throws InvalidOrExpiredTokenException {
        Optional<Session> tokenOptional = sessionRepository.findSessionByToken(token);

        if(tokenOptional.isEmpty()
                || tokenOptional.get().getExpiryAt().compareTo(new Date()) < 0
                || tokenOptional.get().isDeleted()
                || tokenOptional.get().getSessionStatus() == SessionStatus.EXPIRED) {
            throw new InvalidOrExpiredTokenException("Invalid or expired token.");
        }

        Session savedSession = tokenOptional.get();

        savedSession.setDeleted(true);
        savedSession.setDeletedAt(new Date());

        sessionRepository.save(savedSession);
        return ("Logged out successfully.");
    }

    public boolean isValidEmail(String email) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        return emailValidator.isValid(email);
    }
}
