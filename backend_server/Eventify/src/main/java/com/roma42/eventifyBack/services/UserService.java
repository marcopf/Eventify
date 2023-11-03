package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.exception.UserCredentialNotFoundException;
import com.roma42.eventifyBack.exception.UserNotFoundException;
import com.roma42.eventifyBack.repositories.UserCredentialRepository;
import com.roma42.eventifyBack.repositories.UserRepository;
import com.roma42.eventifyBack.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserService {

    final private UserRepository userRepository;
    final private UserCredentialRepository userCredentialRepository;
    final private BCryptPasswordEncoder encoder;



    @Autowired
    public UserService(UserRepository userRepository,
                       UserCredentialRepository userCredentialRepository) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    public List<User> findAllUser() {
        return this.userRepository.findAll();
    }

    public User findUserById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + id + " not found"));
    }

    public User findUserByVerificationCode(String verificationCode) {
        return this.userRepository.findUserByVerificationCode(verificationCode)
                .orElseThrow(() -> new UserNotFoundException("User with validation code: "
                        + verificationCode + " not found"));
    }

    public User findUserByPasswordCode(String passwordCode) {
        return this.userRepository.findUserByPasswordCode(passwordCode)
                .orElseThrow(() -> new UserNotFoundException("User with validation code: "
                        + passwordCode + " not found"));
    }

    public User findUserByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    public User findUserByUploadCode(String uploadCode) {
        return this.userRepository.findUserByUploadCode(uploadCode)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    public void verifyUser(String verificationCode) {
        User user = findUserByVerificationCode(verificationCode);
        if (user == null || user.isEnabled())
            throw new ForbiddenException("user already verified or not registered");

        user.setVerificationCode(null);
        user.setEnabled(true);
        updateUser(user);
    }

    public void passwordVerifyUser(String passwordCode) {
        User user = findUserByPasswordCode(passwordCode);
        if (user == null)
            throw new ForbiddenException("user already verified or not registered");
        user.setPasswordCode(null);
        updateUser(user);
    }

    public User addUser(User user) {
        if (this.userCredentialRepository.findByUsername(user.getUserCredential().getUsername()).isPresent())
            throw new ForbiddenException("user already registered");
        RolesList rolesList = new RolesList();
        rolesList.setRole(new Role(2L, "USER"));
        rolesList.setUser(user);
        UserCredential userCredential = new UserCredential();
        userCredential.setUser(user);
        userCredential.setUsername(user.getUserCredential().getUsername());
        userCredential.setPassword(user.getUserCredential().getPassword());
        user.setUserCredential(userCredential);
        user.setRolesList(rolesList);
        return this.userRepository.save(user);
    }

    public User addUserFromFile(Admin genericUser, String role) {
        User user = new User();
        user.setFirstName(genericUser.getFirstName());
        user.setLastName(genericUser.getLastName());
        user.setEmail(genericUser.getEmail());
        user.setProfilePicture(genericUser.getUsername() + ".jpeg");
        // user credential setting
        UserCredential credential = new UserCredential();
        credential.setUser(user);
        credential.setUsername(genericUser.getUsername());
        String encodedPassword = new BCryptPasswordEncoder(10)
                .encode(genericUser.getPassword());
        credential.setPassword(encodedPassword);
        user.setUserCredential(credential);
        // date of birth setting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        user.setDateOfBirth(LocalDate.parse(genericUser.getDateOfBirth(), formatter));
        // set roles list
        RolesList rolesList = new RolesList();
        if (role.equals("ADMIN"))
            rolesList.setRole(new Role(1L, "ADMIN"));
        else
            rolesList.setRole(new Role(2L, "USER"));
        rolesList.setUser(user);
        user.setRolesList(rolesList);
        // set starting options
        user.setEnabled(true);
        user.setLoggedOut(true);
        user.setBlocked(false);
        user.setVerificationCode(null);
        user.setUploadCode(null);
        return this.userRepository.save(user);
    }

    public User updateUser(User user) {
        UserCredential oldUserCredential = this.userCredentialRepository
                .findByUsername(user.getUserCredential().getUsername())
                .orElseThrow(() -> new UserCredentialNotFoundException("user not found"));
        User oldUser = findUserById(oldUserCredential.getUser_id());
        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setEmail(user.getEmail());
        oldUser.setEnabled(user.isEnabled());
        oldUser.setBlocked(user.isBlocked());
        oldUser.setVerificationCode(user.getVerificationCode());
        oldUser.setPasswordCode(user.getPasswordCode());
        oldUser.setUploadCode(user.getUploadCode());
        return this.userRepository.save(oldUser);
    }


    public User updateUserPicture(Long id, String picture) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        user.setProfilePicture(picture);
        user.setUploadCode(null);
        return this.userRepository.save(user);
    }

    public User updatePlainUser(User user) {
        return this.userRepository.save(user);
    }

    // this function search two times the database, up to now is mandatory
    // and I don't know if exists a better way to update password and user
    public User updateUserWithPassword(User user, String newPassword) {
        UserCredential oldUserCredential = this.userCredentialRepository
                .findByUsername(user.getUserCredential().getUsername())
                .orElseThrow(() -> new UserCredentialNotFoundException("user not found"));
        if (!this.encoder.matches(user.getUserCredential().getPassword(), oldUserCredential.getPassword()))
            throw new ForbiddenException("wrong password");
        oldUserCredential.setPassword(this.encoder.encode(newPassword));
        this.userCredentialRepository.save(oldUserCredential);
        return this.updateUser(user);
    }

    public boolean emailAlreadyRegistered(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
}