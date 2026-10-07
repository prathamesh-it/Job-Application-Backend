package com.JobApplication.JobApplication.Controllers;

import com.JobApplication.JobApplication.DTOs.UserRequest;
import com.JobApplication.JobApplication.DTOs.UserResponse;
import com.JobApplication.JobApplication.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController
{
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserResponse addUser(@Valid @RequestBody UserRequest userRequest)
    {
        System.out.println("Adding a new user");
        return userService.addUser(userRequest);

    }

    @GetMapping("/users")
    public Page<UserResponse> userResponses(@RequestParam(defaultValue = "0") int page , @RequestParam(defaultValue = "3") int size)
    {
        System.out.println("Fetching all the users");
        return userService.getAllUsers(page,size);
    }

    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable long id)
    {
        System.out.println("Fetching user by id: " + id);
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}")
    public UserResponse updateUserById(@Valid @PathVariable long id ,@Valid @RequestBody UserRequest userRequest)
    {
        System.out.println("Updating user by id: " + id);
        return userService.updateUserById(id , userRequest);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUserById(@PathVariable long id)
    {
        System.out.println("Deleting user by id: " + id);
        return userService.deleteUserById(id);
    }

}
