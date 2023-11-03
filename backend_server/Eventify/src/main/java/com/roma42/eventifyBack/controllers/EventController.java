package com.roma42.eventifyBack.controllers;

import com.roma42.eventifyBack.dto.EventDto;
import com.roma42.eventifyBack.dto.SearchResultDto;
import com.roma42.eventifyBack.exception.BadRequestException;
import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.exception.StorageException;
import com.roma42.eventifyBack.exception.UploadException;
import com.roma42.eventifyBack.mappers.EventMapper;
import com.roma42.eventifyBack.models.*;
import com.roma42.eventifyBack.paginations.PaginationUtil;
import com.roma42.eventifyBack.services.*;
import com.roma42.eventifyBack.uploaders.StorageService;
import com.roma42.eventifyBack.validators.ImageValidator;
import com.google.maps.errors.ApiException;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/event")
@CrossOrigin(origins = "${client.protocol}://${client.ip}:${client.port}", allowCredentials = "true")
public class EventController {

    final private EventService eventService;
    final private UserService userService;
    final private UserCredentialService userCredentialService;
    final private CategoryService categoryService;
    final private StorageService storageService;
    final private PictureService pictureService;
    final private NotificationService notificationService;
    final private EventCategoriesService eventCategoriesService;
    @Value("${max.events}")
    private Long maxEvents;

    @Autowired
    public EventController(EventService eventService, UserService userService,
                           CategoryService categoryService,
                           UserCredentialService userCredentialService,
                           StorageService storageService,
                           PictureService pictureService,
                           NotificationService notificationService,
                           EventCategoriesService eventCategoriesService) {
        this.eventService = eventService;
        this.userService = userService;
        this.userCredentialService = userCredentialService;
        this.categoryService = categoryService;
        this.storageService = storageService;
        this.pictureService = pictureService;
        this.notificationService = notificationService;
        this.eventCategoriesService = eventCategoriesService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventDto>> getAllEvent(@RequestParam(name = "type", required = false) String type,
                                                      @RequestParam(name = "search", required = false) String search,
                                                      @RequestParam(name = "from", required = false) String from,
                                                      @RequestParam(name = "to", required = false) String to,
                                                      @RequestParam(name = "categories",
                                                              required = false) Set<String> categories,
                                                      @PageableDefault(size = 1000) Pageable pageable) {
        Map<String, String> params = Map.of(
                "type", type == null ? "" : type,
                "search", search == null ? "" : search,
                "from", from == null ? "" : from,
                "to", to == null ? "" : to
        );
        Slice<Event> events = this.eventService.searchEngine(params, categories, pageable);
        HttpHeaders headers = PaginationUtil.generateInfiniteScrollHeader(events);
        // alternative
        SearchResultDto searchResultDto = new SearchResultDto(events.hasNext(),
                EventMapper.eventSliceToMinimalDtoList(events));
        // has next in header
        return new ResponseEntity<>(EventMapper.eventSliceToMinimalDtoList(events), headers, HttpStatus.OK);
    }

    @GetMapping("/registered")
    public ResponseEntity<List<EventDto>> getRegisteredEvent(Authentication auth) {
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        List<Event> events = new ArrayList<>();
        for (ParticipantList participantList : user.getEvents()) {
            events.add(participantList.getEvent());
        }
        events.sort(Comparator.comparing(Event::getEventDate));
        List<EventDto> eventsDto = EventMapper.eventListToMinimalDtoList(events);
        return new ResponseEntity<>(eventsDto, HttpStatus.OK);
    }

    @GetMapping("/owned")
    public ResponseEntity<List<EventDto>> getOwnedEvent(Authentication auth,
                                                        Pageable pageable) {
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        Slice<Event> events = this.eventService.findSliceByOwnerId(user, pageable);
        List<EventDto> eventsDto = EventMapper.eventSliceToMinimalDtoList(events);
        return new ResponseEntity<>(eventsDto, HttpStatus.OK);
    }

    @GetMapping("/imminent")
    public ResponseEntity<List<EventDto>> getImminentEvent(Authentication auth) {
        User user = this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser();
        List<Event> events = new ArrayList<>();
        for (ParticipantList participantList : user.getEvents()) {
            if (participantList.getEvent().getEventDate().isBefore(LocalDateTime.now().plusWeeks(1L)))
                events.add(participantList.getEvent());
        }
        events.sort(Comparator.comparing(Event::getEventDate));
        List<EventDto> eventsDto = EventMapper.eventListToMinimalDtoList(events);
        return new ResponseEntity<>(eventsDto, HttpStatus.OK);
    }

//    @CrossOrigin()
    @GetMapping("")
    public ResponseEntity<EventDto> getEventById(@RequestParam("id") Long id,
                                                 Authentication auth) {
        Event event = this.eventService.findEventById(id);
        EventDto eventDto = EventMapper.eventToDto(event, auth.getName());
        return new ResponseEntity<>(eventDto, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<EventDto> addEvent(@RequestBody @Valid EventDto eventDto,
                                             Authentication auth) throws IOException,
            ApiException, InterruptedException {
        UserCredential userCredential = this.userCredentialService.findUserCredentialByUsername(auth.getName());
        User user = this.userService.findUserById(userCredential.getUser_id());
        if (user.getOwnedEvents().size() >= maxEvents)
            throw new ForbiddenException("max events reached");
        Event event = EventMapper.dtoToEvent(eventDto);
        event.setUser(user);
        if (event.getEventDate().isBefore(LocalDateTime.now())
                || event.getEventDate().isAfter(LocalDateTime.now().plusYears(1L)))
            throw  new BadRequestException("invalid date");
        // categories search
        Set<Category> categories = this.categoryService.findCategoriesByNames(eventDto.getCategories());
        // add owner to participant list
        ParticipantList participantList = ParticipantListService.createMockParticipantList(user, event);
        // create event
        Event newEvent = this.eventService.addEvent(event, participantList, categories);
        return new ResponseEntity<>(EventMapper.eventToDto(newEvent, userCredential.getUsername()), HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/upload", consumes = {
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/heif",
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<?> uploadGallery(@RequestParam("eventId") Long eventId,
                                           @RequestPart MultipartFile[] image,
                                           Authentication auth) {
        // check that the user sending this request is the owner of the event
        Event event = this.eventService.findEventById(eventId);
        if (!event.getUser().getUserCredential().getUsername().equals(auth.getName()))
            throw new ForbiddenException("Forbidden");
        if (image.length > 5)
            throw new UploadException("Too many files passed");
        this.storageService.deleteAllFile(event.getPictures().stream()
                .map(Picture::getUri).collect(Collectors.toList()), "event");
        for (Picture picture : event.getPictures().stream().toList()) {
            event.removePicture(picture);
            this.pictureService.deletePicture(picture.getPictureId());
        }
        event = this.eventService.updateEvent(event);
        String uri;
        for (int i = 0; i < image.length; i++) {
            try {
                ImageValidator.validate(image[i]);
            } catch (UploadException e) {
                continue;
            }
            uri = "event" + eventId + "_" + (event.getPictures().size() + i) + "."
                    + FilenameUtils.getExtension(image[i].getOriginalFilename());
            this.storageService.upload(image[i], uri, "event");
            this.pictureService.addPicture(new Picture(null, uri, event));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/image")
    @ResponseBody
    public ResponseEntity<Resource> loadImage(@RequestParam("eventId") Long eventId,
                                              @RequestParam("imageNum") Integer imageNum) {
        Event event = this.eventService.findEventById(eventId);
        List<Picture> pictures = new ArrayList<>(event.getPictures().stream().toList());
        ImageValidator.validatePicturesList(pictures, imageNum);
        pictures.sort(Comparator.comparing(Picture::getUri));
        Resource image = this.storageService.loadAsResource(pictures.get(imageNum).getUri(), "event");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/"
                        + FilenameUtils.getExtension(pictures.get(imageNum).getUri())))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @Transactional
    @PutMapping("/edit")
    public ResponseEntity<EventDto> editEventById(@RequestParam("id") Long id,
                                                  @RequestBody @Valid EventDto eventDto,
                                                  Authentication auth) throws IOException,
            ApiException, InterruptedException {
        // check that the user sending this request is the owner of the event
        Event event = this.eventService.findEventById(id);
        Set<User> participants = event
                .getParticipants()
                .stream()
                .map(ParticipantList::getUser)
                .collect(Collectors.toSet());
        participants.remove(event.getUser());
        if (!event.getUser().getUserCredential().getUsername().equals(auth.getName())) {
            throw new ForbiddenException("Forbidden");
        }
        Event sentEvent = EventMapper.dtoToEvent(eventDto);
        System.out.println(eventDto.getCategories());
        Set<Category> categories = this.categoryService
                .findCategoriesByNames(eventDto.getCategories());
        System.out.println(eventDto.getCategories());
        event.substituteCategories(new HashSet<>());
        this.eventCategoriesService.deleteEventCategoriesByEvent(event);
        System.out.println(event.getCategories());
        event.substituteCategories(EventCategoriesService.categoriesToEventCategories(categories, event));
        // notification
        if (event.modified(sentEvent)) {
            for (User user : participants) {
                Notification notification = this.notificationService.generateNotification(event, sentEvent);
                notification.setUser(user);
                this.notificationService.addNotification(notification);
            }
        }
        Event updatedEvent = this.eventService.updateEventWithSent(event, sentEvent);
        return new ResponseEntity<>(EventMapper.eventToDto(updatedEvent, auth.getName()), HttpStatus.OK);
    }

    @PutMapping("/categories/edit")
    public ResponseEntity<EventDto> editCategories(@RequestParam("id") Long id,
                                                   @RequestBody EditCategoriesForm editCategoriesForm,
                                                   Authentication auth) {
        // check that the user sending this request is the owner of the event
        Event event = this.eventService.findEventById(id);
        if (!event.getUser().getUserCredential().getUsername().equals(auth.getName()))
            throw new ForbiddenException("Forbidden");
        Set<Category> categories = this.categoryService
                .findCategoriesByNames(new HashSet<>(editCategoriesForm.getToEdit()));
        event.substituteCategories(EventCategoriesService.categoriesToEventCategories(categories, event));
        Event newEvent = this.eventService.updateEvent(event);
        return new ResponseEntity<>(EventMapper.eventToDto(newEvent, auth.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEvent(@RequestParam("id") Long id,
                                         Authentication auth) {
        // check that the user sending this request is the owner of the event
        Event event = this.eventService.findEventById(id);
        Set<User> participants = event.getParticipants()
                .stream()
                .map(ParticipantList::getUser)
                .collect(Collectors.toSet());
        participants.remove(event.getUser());
        if (!event.getUser().getUserCredential().getUsername().equals(auth.getName())
                && !auth.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_ADMIN")))
            throw new ForbiddenException("Forbidden");
        try {
            this.storageService.deleteAllFile(event.getPictures().stream()
                    .map(Picture::getUri).collect(Collectors.toList()), "event");
        } catch (StorageException ignore) {}
        // notification
        for (User participant : participants) {
            Notification notification = this.notificationService.generateNotificationDeleted(event);
            notification.setUser(participant);
            this.notificationService.addNotification(notification);
        }
        this.eventService.deleteEvent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}