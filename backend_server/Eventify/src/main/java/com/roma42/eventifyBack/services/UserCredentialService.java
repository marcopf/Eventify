package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.dto.LoginDto;
import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.exception.UserCredentialNotFoundException;
import com.roma42.eventifyBack.repositories.UserCredentialRepository;
import com.roma42.eventifyBack.models.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
public class UserCredentialService {

    final private UserCredentialRepository userCredentialRepository;
    final private EmailService emailService;
    final private BCryptPasswordEncoder encoder;

    @Autowired
    public UserCredentialService(UserCredentialRepository userCredentialRepository,
                                 EmailService emailService) {
        this.userCredentialRepository = userCredentialRepository;
        this.emailService = emailService;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    public UserCredential findUserCredentialById(Long id) {
        return this.userCredentialRepository.findById(id)
                .orElseThrow(() -> new UserCredentialNotFoundException("Not found"));
    }

    public UserCredential findUserCredentialByUsername(String username) {
        return this.userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new UserCredentialNotFoundException("Not found"));
    }

    public List<UserCredential> findAllUserCredential() {
        return this.userCredentialRepository.findAll();
    }

    public UserCredential addUserCredential(UserCredential userCredential) {
        return this.userCredentialRepository.save(userCredential);
    }

    public UserCredential updateUserCredential(UserCredential userCredential) {
        return this.userCredentialRepository.save(userCredential);
    }

    public void deleteUserCredentialById(Long id) {
        this.userCredentialRepository.deleteById(id);
    }

    public UserCredential loginUser(LoginDto loginDto) {
        UserCredential userCredential = this.userCredentialRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new ForbiddenException("Invalid username or password"));
        User user = userCredential.getUser();
        if (!this.encoder.matches(loginDto.getPassword(), userCredential.getPassword()))
            throw new ForbiddenException("Invalid username or password");
        if (!user.isEnabled())
            throw new ForbiddenException("Please verify your email address");
        if (user.isBlocked())
            throw new ForbiddenException("You may be blocked");
        return userCredential;
    }



    public User userRegistration(User user, String siteURL) throws MessagingException,
            UnsupportedEncodingException{
        if (Period.between(user.getDateOfBirth(), LocalDate.now()).getYears() < 18)
            throw new ForbiddenException("invalid age");
        user.setEnabled(false);
        user.setLoggedOut(true);
        user.setBlocked(false);
        String encodedPassword = this.encoder.encode(user.getUserCredential().getPassword());
        user.getUserCredential().setPassword(encodedPassword);
        String randomCode = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationCode(randomCode);
        randomCode = UUID.randomUUID().toString().replace("-", "");
        user.setUploadCode(randomCode);
        this.emailService.sendVerificationEmail(user, siteURL);
        return user;
    }

    public User passwordRecovery(User user, String siteURL) throws MessagingException,
            UnsupportedEncodingException{
        String randomCode = UUID.randomUUID().toString().replace("-", "");
        user.setPasswordCode(randomCode);
        this.emailService.sendPasswordEmail(user, siteURL);
        return user;
    }

    public UserCredential saveNewPassword(String username, String newPassword) {
        UserCredential userCredential = this.findUserCredentialByUsername(username);
        userCredential.setPassword(this.encoder.encode(newPassword));
        return this.userCredentialRepository.save(userCredential);
    }

    public boolean usernameAlreadyRegistered(String username) {
        return this.userCredentialRepository.findByUsername(username).isPresent();
    }
}
