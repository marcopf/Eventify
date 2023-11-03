package com.roma42.eventifyBack.controllers;

import com.roma42.eventifyBack.dto.MinimalUserDto;
import com.roma42.eventifyBack.dto.NotificationDto;
import com.roma42.eventifyBack.dto.UserDto;
import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.exception.StorageException;
import com.roma42.eventifyBack.exception.UploadException;
import com.roma42.eventifyBack.mappers.MinimalUserMapper;
import com.roma42.eventifyBack.mappers.NotificationMapper;
import com.roma42.eventifyBack.mappers.UserMapper;
import com.roma42.eventifyBack.models.*;
import com.roma42.eventifyBack.paginations.PaginationUtil;
import com.roma42.eventifyBack.services.*;
import com.roma42.eventifyBack.uploaders.StorageService;
import com.roma42.eventifyBack.validators.ImageValidator;
import com.roma42.eventifyBack.validators.RegisterValidation;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/user")
@CrossOrigin(origins = "${client.protocol}://${client.ip}:${client.port}", allowCredentials = "true")
public class UserController {

    final private UserService userService;
    final private EventService eventService;
    final private ParticipantListService participantListService;
    final private UserCredentialService userCredentialService;
    final private StorageService storageService;

    final private NotificationService notificationService;

    @Autowired
    public UserController(UserService userService, EventService eventService,
                          ParticipantListService participantListService,
                          UserCredentialService userCredentialService,
                          StorageService storageService,
                          NotificationService notificationService) {
        this.userService = userService;
        this.eventService = eventService;
        this.participantListService = participantListService;
        this.userCredentialService = userCredentialService;
        this.storageService = storageService;
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<MinimalUserDto>> getAllUser(Authentication auth) {
        // check admin permission
        List<User> users = this.userService.findAllUser();
        List<MinimalUserDto> usersDto = new ArrayList<>();
        users.forEach((user) -> usersDto.add(MinimalUserMapper.toDto(user)));
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

//    @GetMapping("/id/{id}")
//    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
//        UserDto userDto = UserMapper.toDto(this.userService.findUserById(id));
//        return new ResponseEntity<>(userDto, HttpStatus.OK);
//    }

    @GetMapping("")
    public ResponseEntity<UserDto> getUserByUsername(@RequestParam("username") String username,
                                                     Authentication auth) {
        // check user or admin permission
        if (!auth.getName().equals(username)
                && !auth.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_ADMIN")))
            throw new ForbiddenException("Forbidden get user");
        User user = this.userCredentialService.findUserCredentialByUsername(username).getUser();
        return new ResponseEntity<>(UserMapper.toDto(user), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDto userDto,
                                              Authentication auth) {
        // check user permission
        User user = UserMapper.toUserWithoutDate(userDto);
        if (!auth.getName().equals(userDto.getUsername()))
             throw new ForbiddenException("Forbidden");
        UserDto updatedUserDto;
        if (userDto.getNewPassword() == null) {
            updatedUserDto = UserMapper.toDto(this.userService.updateUser(user));
        } else {
            updatedUserDto = UserMapper.toDto(this.userService.updateUserWithPassword(user, userDto.getNewPassword()));
        }
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = {
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/heif",
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile[] image,
                                         Authentication auth,
                                         @RequestHeader Map<String, String> headers) {
        // check user permission
        System.out.println(headers);
        UserCredential userCredential = this.userCredentialService.findUserCredentialByUsername(auth.getName());
        User user = this.userService.findUserById(userCredential.getUser_id());
        if (image.length > 1)
            throw new UploadException("Too many files passed");
        ImageValidator.validate(image[0]);
        String uri = auth.getName() + "." + FilenameUtils.getExtension(image[0].getOriginalFilename());
        this.storageService.upload(image[0], uri, "user");
        if (!user.getProfilePicture().isEmpty() && !user.getProfilePicture().equals(uri))
            this.storageService.deleteFile(user.getProfilePicture(), "user");
        this.userService.updateUserPicture(userCredential.getUser_id(), uri);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/image")
    @ResponseBody
    public ResponseEntity<Resource> loadImage(@RequestParam("username") String username,
                                              @RequestHeader Map<String, String> headers
                                              ) {
        UserCredential userCredential = this.userCredentialService.findUserCredentialByUsername(username);
        User user = this.userService.findUserById(userCredential.getUser_id());
        Resource image;
        if (user.getProfilePicture().isEmpty()) {
            image = this.storageService.loadAsResource("default.jpeg", "user");
        } else {
            image = this.storageService.loadAsResource(user.getProfilePicture(), "user");
        }
        String ext = user.getProfilePicture().isEmpty() ? "jpeg" : FilenameUtils.getExtension(user.getProfilePicture());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/" + ext))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @PutMapping("/register")
    public ResponseEntity<UserDto> registerUserToEvent(@RequestParam("eventId") Long eventId,
                                                       Authentication auth) {
        // check if user has permission
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        if (!auth.getName().equals(user.getUserCredential().getUsername()))
            throw new ForbiddenException("Forbidden");
        Event event = this.eventService.findEventById(eventId);
        // check if user isn't already registered
        RegisterValidation.validateRegistration(user, event);
        // create participant list for user
        ParticipantList participantList = this.participantListService
                .addParticipantList(ParticipantListService.createMockParticipantList(user, event));
        // add participant list to user
        user.appendEvent(participantList);
        // update user
        User updatedUser = this.userService.updateUser(user);
        return new ResponseEntity<>(UserMapper.toDto(updatedUser), HttpStatus.OK);
    }

    @PutMapping("/unregister")
    public ResponseEntity<UserDto> unregisterEvent(@RequestParam("eventId") Long eventId,
                                                   Authentication auth) {
        // check if user has permission
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        if (!auth.getName().equals(user.getUserCredential().getUsername()))
            throw new ForbiddenException("Forbidden");
        Event event = this.eventService.findEventById(eventId);
        RegisterValidation.validateUnsubscribe(user, event);
        ParticipantListId participantListId = new ParticipantListId(user.getId(), event.getEventId());
        for (ParticipantList pl : user.getEvents()) {
            if (pl.getParticipantListId().equals(participantListId)) {
                user.removeEvent(pl);
                event.removeParticipant(pl);
                User updatedUser = this.userService.updateUser(user);
                this.eventService.updateEvent(event);
                return new ResponseEntity<>(UserMapper.toDto(updatedUser), HttpStatus.OK);
            }
        }
        throw new ForbiddenException("Not registered to this event");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserById(@RequestParam("username") String username,
                                            Authentication auth) {
        // check if user has permission
        User user = this.userCredentialService.findUserCredentialByUsername(username).getUser();
        try {
            if (!user.getProfilePicture().isEmpty())
                this.storageService.deleteFile(user.getProfilePicture(), "user");
        } catch (StorageException ignored) {}
        this.userService.deleteUser(user.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/block")
    public ResponseEntity<?> blockUser(@RequestParam("username") String username) {
        User user = this.userCredentialService.findUserCredentialByUsername(username).getUser();
        user.setBlocked(true);
        this.userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/unblock")
    public ResponseEntity<?> unblockUser(@RequestParam("username") String username) {
        User user = this.userCredentialService.findUserCredentialByUsername(username).getUser();
        user.setBlocked(false);
        this.userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/notification")
    public ResponseEntity<List<NotificationDto>> notifications(@RequestParam(value = "delete", required = false)
                                                                   String delete,
                                                               Authentication auth) {
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        List<Notification> notifications = this.notificationService.findNotificationByUser(user);
        List<NotificationDto> notificationsDto = NotificationMapper.listToDtoList(notifications);
        if (delete != null && delete.equals("true"))
            this.notificationService.deleteNotificationByUser(user);
        return new ResponseEntity<>(notificationsDto, HttpStatus.OK);
    }
}