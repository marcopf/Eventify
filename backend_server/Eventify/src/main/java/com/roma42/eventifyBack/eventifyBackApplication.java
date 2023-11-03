package com.roma42.eventifyBack;

import com.roma42.eventifyBack.dto.EventDto;
import com.roma42.eventifyBack.exception.BadRequestException;
import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.mappers.EventMapper;
import com.roma42.eventifyBack.models.*;
import com.roma42.eventifyBack.services.*;
import com.roma42.eventifyBack.uploaders.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@EnableAsync
public class eventifyBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(eventifyBackApplication.class, args);
	}
	@Bean
	CommandLineRunner init(@Value(value = "${demo.application}") String demo,
						   CategoryService categoryService,
						   EventService eventService,
						   UserService userService,
						   PictureService pictureService,
						   RolesListService rolesListService) {
		return (args) -> {
			ObjectMapper objectMapper = new ObjectMapper();
			Resource resource = new ClassPathResource("admin.JSON");
			if (!rolesListService.isUserWithRole(new Role(1L,""))) {
				try (InputStream inputStream = resource.getInputStream()) {
					Admin admin = objectMapper.readValue(inputStream, Admin.class);
					User adminReg = userService.addUserFromFile(admin, "ADMIN");
					System.out.println("admin correctly registered");
				}
			}
			if (demo.equals("true")) {
				String file = "demo/users/user";
				List<User> users = new ArrayList<>();
				for (int i = 0; i < 20; i++) {
					resource = new ClassPathResource(file + i + ".JSON");
					try (InputStream inputStream = resource.getInputStream()) {
						Admin user = objectMapper.readValue(inputStream, Admin.class);
						users.add(userService.addUserFromFile(user, "USER"));
						System.out.println("demo user " + (i + 1) + " correctly registered");
					}
				}
				file = "demo/events/event";
				for (int i = 0; i < 20; i++) {
					resource = new ClassPathResource(file + i + ".JSON");
					try (InputStream inputStream = resource.getInputStream()) {
						EventDto eventDto = objectMapper.readValue(inputStream, EventDto.class);
						Event event = EventMapper.dtoToEvent(eventDto);
						event.setUser(users.get(i));
						// categories search
						Set<Category> categories = categoryService.findCategoriesByNames(eventDto.getCategories());
						// add owner to participant list
						ParticipantList participantList = ParticipantListService
								.createMockParticipantList(users.get(i), event);
						// create event
						Event newEvent = eventService.addEvent(event, participantList, categories);
						pictureService.addPicture(new Picture(null, "image1.jpeg", newEvent));
						pictureService.addPicture(new Picture(null, "image2.jpeg", newEvent));
						pictureService.addPicture(new Picture(null, "image3.jpeg", newEvent));
						System.out.println("demo event " + (i + 1) + " correctly registered");
					}
				}
			}
		};
	}
}
