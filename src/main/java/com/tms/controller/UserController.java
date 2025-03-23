package com.tms.controller;

import com.tms.model.User;
import com.tms.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/create")
    public String getUserCreatePage(Model model) {
        model.addAttribute("user", new User());
        return "createUser";
    }


    @GetMapping
    public String getUserUpdatePage(@RequestParam("userId") Long userId, Model model, HttpServletResponse response) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
            model.addAttribute("message", "User not found: id=" + userId);
            return "innerError";
        }
        model.addAttribute("user", user.get());
        return "user"; // Рендерит
    }

    //Create
    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, HttpServletResponse response, Model model) {
        Optional<User> createdUser = userService.createUser(user);
        if (createdUser.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            model.addAttribute("message", "User not created");
            return "innerError";
        }
        model.addAttribute("user", createdUser.get());
        return "user";
    }

    //Read
    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404
            model.addAttribute("message", "User not found: id=" + id);
            return "innerError";
        }
        response.setStatus(HttpServletResponse.SC_OK); //200
        model.addAttribute("user", user.get());
        return "user";
    }

    //Update
    @PostMapping
    public String updateUser(@ModelAttribute("user") User user, Model model, HttpServletResponse response) {
        Optional<User> userUpdated = userService.updateUser(user);
        if (userUpdated.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            model.addAttribute("message", "User not updated");
            return "innerError";
        }
        response.setStatus(HttpServletResponse.SC_OK);
        model.addAttribute("user", userUpdated.get());
        return "user";
    }

    @GetMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId,
                             Model model,
                             HttpServletResponse response) {
        try {
            System.out.println("Started deleting user, ID: " + userId);
            Optional<User> userDeleted = userService.deleteUser(userId);
            if (userDeleted.isEmpty()) {
                System.out.println("User with ID not found or not deleted" + userId);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                model.addAttribute("message", "User not deleted");
                return "innerError";
            }
            System.out.println("User successfully deleted, ID: " + userId);
            response.setStatus(HttpServletResponse.SC_OK);
            model.addAttribute("user", userDeleted.get());
            return "user";
        } catch (Exception e) {
            System.out.println("Error while trying to delete user with ID:" + userId + e.getMessage() + e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            model.addAttribute("message", "Internal server error while deleting user");
            return "innerError";
        }
    }

    @GetMapping("/allusers")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            model.addAttribute("message", "No users in the database");
        }
        model.addAttribute("users", users);
        return "listUsers";
    }
}
