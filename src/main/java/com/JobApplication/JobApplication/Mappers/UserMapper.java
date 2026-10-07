package com.JobApplication.JobApplication.Mappers;

import com.JobApplication.JobApplication.DTOs.UserRequest;
import com.JobApplication.JobApplication.DTOs.UserResponse;
import com.JobApplication.JobApplication.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper
{
    public User toEntity(UserRequest userRequest) {
        User user = new User();

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setPassword(userRequest.getPassword());

        return user;
    }

    public UserResponse toResponse(User user)
    {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());

        return userResponse;
    }
}



