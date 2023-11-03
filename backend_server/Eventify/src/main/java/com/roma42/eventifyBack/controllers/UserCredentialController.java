package com.roma42.eventifyBack.controllers;

import com.roma42.eventifyBack.dto.LoginDto;
import com.roma42.eventifyBack.dto.UserDto;
import com.roma42.eventifyBack.exception.EmailException;
import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.exception.UploadException;
import com.roma42.eventifyBack.exception.UserCredentialNotFoundException;
import com.roma42.eventifyBack.mappers.UserMapper;
import com.roma42.eventifyBack.models.User;
import com.roma42.eventifyBack.models.UserCredential;
import com.roma42.eventifyBack.readers.FileReader;
import com.roma42.eventifyBack.security.JwtService;
import com.roma42.eventifyBack.services.UserCredentialService;
import com.roma42.eventifyBack.services.UserService;
import com.roma42.eventifyBack.uploaders.StorageService;
import com.roma42.eventifyBack.validators.ImageValidator;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/api/v1/credential")
@CrossOrigin(origins = "${client.protocol}://${client.ip}:${client.port}", allowCredentials = "true")
public class UserCredentialController {
    final static private Logger log = LoggerFactory.getLogger(UserCredentialController.class);
    final private UserCredentialService userCredentialService;
    final private UserService userService;
    final private JwtService jwtService;
    final private StorageService storageService;
    @Value("${client.ip}")
    private String ipAddress;
    @Value("${client.port}")
    private String clientPort;
    @Value("${client.protocol}")
    private String clientProtocol;
    @Autowired
    public UserCredentialController(UserCredentialService userCredentialService,
                                    UserService userService,
                                    JwtService jwtService,
                                    StorageService storageService) {
        this.userCredentialService = userCredentialService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.storageService = storageService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginDto loginDto) {
        System.out.println("loginDto = " + loginDto);
        System.out.println(">> username: [" + loginDto.getUsername() + "]");
        System.out.println(">> password: [" + loginDto.getPassword() + "]");
        UserCredential userCredential = this.userCredentialService.loginUser(loginDto);
        User user = userCredential.getUser();
        HttpHeaders headers = this.jwtService.generateTokens(userCredential);
        user.setLoggedOut(false);
        log.debug("user [" + userCredential.getUsername() + "] logged in");
        user = this.userService.updateUser(user);
        return ResponseEntity.ok()
                .headers(headers)
                .body(UserMapper.toDto(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(Authentication auth) {
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        if (user.isLoggedOut())
            throw new ForbiddenException("User already logged out");
        user.setLoggedOut(true);
        log.debug("user [" + auth.getName() + "] logged out");
        HttpHeaders headers = this.jwtService.generateLogoutTokens();
        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid UserDto userDto,
                                          HttpServletRequest request) {
        if (this.userCredentialService.usernameAlreadyRegistered(userDto.getUsername())
                || this.userService.emailAlreadyRegistered(userDto.getEmail()))
            throw new ForbiddenException("Username or email already in use");
        User user = UserMapper.toUser(userDto);
        String siteURL = getSiteURL(request);
        try {
            user = this.userCredentialService.userRegistration(user, siteURL);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new EmailException("Error during email sending");
        }
        UserDto newUserDto = UserMapper.toDto(this.userService.addUser(user));
        newUserDto.setUploadCode(user.getUploadCode());
        return new ResponseEntity<>(newUserDto, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyRegistration(@RequestParam("token") String verificationCode) {
        this.userService.verifyUser(verificationCode);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, clientProtocol + "://" + ipAddress + ":" + clientPort + "/login")
                .build();
    }

    @PostMapping(value = "/upload", consumes = {
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/heif",
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("token") String uploadCode,
                                                  @RequestPart MultipartFile[] image) {
        User user = this.userService.findUserByUploadCode(uploadCode);
        if (image.length > 1)
            throw new UploadException("Too many files passed");
        ImageValidator.validate(image[0]);
        String uri = user.getUserCredential().getUsername()
                + "." + FilenameUtils.getExtension(image[0].getOriginalFilename());
        this.storageService.upload(image[0], uri, "user");
        if (!user.getProfilePicture().isEmpty() && !user.getProfilePicture().equals(uri))
            this.storageService.deleteFile(user.getProfilePicture(), "user");
        this.userService.updateUserPicture(user.getId(), uri);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader (name = "cookie") String cookies) {
        if (cookies == null || !cookies.contains("refresh="))
            throw new ForbiddenException("refresh token not found");
        String token = this.jwtService.getTokenFromCookie(cookies, "refresh");
        String username = this.jwtService.extractClaim(token, "sub");
        UserCredential userCredential;
        try {
            userCredential = this.userCredentialService.findUserCredentialByUsername(username);
        } catch (UserCredentialNotFoundException e) {
            HttpHeaders headers = this.jwtService.generateLogoutTokens();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .headers(headers)
                    .build();
        }
        if (!userCredential.getUser().isLoggedOut()
                && !userCredential.getUser().isBlocked()
                && this.jwtService.extractClaim(token, "scope").equals("REFRESH")
                && userCredential.getUser().getRolesList().getRole().getRoleName().equals("USER")) {
            log.debug("JWT refreshed correctly [" + username + "]");
            ResponseCookie newToken = this.jwtService.generateAccessToken(userCredential);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, newToken.toString())
                    .build();
        }
        log.debug("cannot refresh JWT [" + username + "]");
        HttpHeaders headers = this.jwtService.generateLogoutTokens();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .headers(headers)
                .build();
    }

    @PostMapping("/Recovery")
    public ResponseEntity<?> recoveryPassword(@RequestBody @Valid UserDto userDto,
                                              HttpServletRequest request) {
        User user = this.userService.findUserByEmail(userDto.getEmail());
        String siteURL = getSiteURL(request);
        try {
            user = this.userCredentialService.passwordRecovery(user, siteURL);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new EmailException("Error during email sending");
        }
        this.userService.addUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/newPassword")
    public ResponseEntity<String> newPasswordPage(@RequestParam("token") String token) {
        String newPasswordHtml;
        try {
            newPasswordHtml = FileReader.readFile("newPassword.html");
        } catch (IOException e) {
            return new ResponseEntity<>("Error while reading file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        this.userService.findUserByPasswordCode(token);
        return new ResponseEntity<>(newPasswordHtml, HttpStatus.OK);
    }

    @PostMapping("/sendPassword")
    public ResponseEntity<?> setNewPassword(@RequestParam("token") String token,
                                            @RequestBody UserDto userDto) {
        User user = this.userService.findUserByPasswordCode(token);
        this.userCredentialService.saveNewPassword(user.getUserCredential().getUsername(), userDto.getNewPassword());
        this.userService.passwordVerifyUser(token);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, clientProtocol + "://" + ipAddress + ":" + clientPort + "/login")
                .build();
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
