package com.roma42.eventifyBack.controllers;

import com.roma42.eventifyBack.dto.CategoryDto;
import com.roma42.eventifyBack.models.CategoriesList;
import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.mappers.CategoryMapper;
import com.roma42.eventifyBack.models.Category;
import com.roma42.eventifyBack.models.Notification;
import com.roma42.eventifyBack.models.User;
import com.roma42.eventifyBack.paginations.PaginationUtil;
import com.roma42.eventifyBack.services.CategoryService;
import com.roma42.eventifyBack.services.NotificationService;
import com.roma42.eventifyBack.services.UserCredentialService;
import com.roma42.eventifyBack.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/category")
@CrossOrigin(origins = "${client.protocol}://${client.ip}:${client.port}", allowCredentials = "true")
public class CategoryController {

    final private CategoryService categoryService;
    final private UserCredentialService userCredentialService;
    final private NotificationService notificationService;
    final private UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService,
                              UserCredentialService userCredentialService,
                              NotificationService notificationService,
                              UserService userService) {
        this.categoryService = categoryService;
        this.userCredentialService = userCredentialService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<CategoriesList> getAllCategories() {
        List<Category> categories = this.categoryService.findAllCategory();
        CategoriesList categoriesList = new CategoriesList(categories.stream().map(Category::getCategoryName).toList());
        return new ResponseEntity<>(categoriesList, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Valid CategoryDto categoryDto,
                                                   Authentication auth) {
        if (this.userCredentialService.findUserCredentialByUsername(auth.getName()).getUser().isLoggedOut())
            throw new ForbiddenException("Admin logged out");
        Category newCategory = this.categoryService.addCategory(CategoryMapper.ToCategory(categoryDto));
        return new ResponseEntity<>(CategoryMapper.ToMinimalDto(newCategory), HttpStatus.OK);
    }

    @PostMapping("/addList")
    public ResponseEntity<CategoriesList> addListCategories(@RequestBody CategoriesList categoriesList) {
        List<Category> addedCategories = this.categoryService.addCategories(categoriesList.getCategories());
        //notification
        for (User user : this.userService.findAllUser()) {
            Notification notification = this.notificationService.generateNotificationCreateCategories(addedCategories);
            notification.setUser(user);
            this.notificationService.addNotification(notification);
        }
        //return
        CategoriesList addedCategoriesList = new CategoriesList(addedCategories.stream()
                .map(Category::getCategoryName).collect(Collectors.toList()));
        return new ResponseEntity<>(addedCategoriesList, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<CategoryDto> updateCategory(@RequestParam("categoryName") String categoryName,
                                                      @RequestBody @Valid CategoryDto categoryDto) {
        Category oldCategory = this.categoryService.findCategoryByName(categoryName);
        for (User user : this.userService.findAllUser()) {
            Notification notification = this.notificationService.generateNotificationUpdateCategory(categoryName,
                    categoryDto.getCategoryName());
            notification.setUser(user);
            this.notificationService.addNotification(notification);
        }
        oldCategory.setCategoryName(categoryDto.getCategoryName());
        Category newCategory = this.categoryService.addCategory(oldCategory);
        return new ResponseEntity<>(CategoryMapper.ToMinimalDto(newCategory), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCategory(@RequestParam("categoryName") String categoryName) {
        Category category = this.categoryService.findCategoryByName(categoryName);
        this.categoryService.deleteCategory(category.getCategoryId());
        //notification
        for (User user : this.userService.findAllUser()) {
            Notification notification = this.notificationService.generateNotificationDeleteCategory(categoryName);
            notification.setUser(user);
            this.notificationService.addNotification(notification);
        }
        // return
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
