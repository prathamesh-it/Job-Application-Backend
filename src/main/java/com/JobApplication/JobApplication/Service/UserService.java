package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.UserRequest;
import com.JobApplication.JobApplication.DTOs.UserResponse;
import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Exceptions.EmailAlreadyExistException;
import com.JobApplication.JobApplication.Exceptions.ResourceNotFound;
import com.JobApplication.JobApplication.Exceptions.UserNotFoundException;
import com.JobApplication.JobApplication.Mappers.UserMapper;
import com.JobApplication.JobApplication.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService 
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    
    public Page<UserResponse> getAllUsers(int page , int size)
    {
        Pageable pageable = PageRequest.of(page , size);
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);


//        METHOD-2
//        List<UserResponse> userResponses = new ArrayList<>();
//
//        for(User user : users)
//        {
//            UserResponse response = userMapper.toResponse(user);
//            userResponses.add(response);
//        }
//
//        return userResponses;
    }

    public UserResponse addUser(UserRequest userRequest)
    {
        if(userRepository.existsByEmail(userRequest.getEmail()))
        {
            throw new EmailAlreadyExistException("Email already exists: " + userRequest.getEmail());
        }
        return userMapper.toResponse(
                userRepository.save(
                        userMapper.toEntity(userRequest)
                )
        );
    }

    public UserResponse getUserById(long id)
    {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent())
        {
            return userMapper.toResponse(user.get());
        }
        else
        {
            throw new UserNotFoundException("User not found");
        }

    }

    @Transactional
    public UserResponse updateUserById(long id , UserRequest userRequest)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setPassword(userRequest.getPassword());

        return userMapper.toResponse(
                userRepository.save(user)
        );
    }

    public String deleteUserById(long id)
    {
        if(userRepository.existsById(id))
        {
            userRepository.deleteById(id);
            return "User deleted succesfully";
        }
        else
        {
            throw new RuntimeException(("User with id :"+id+" not exist"));
        }

    }
}


//DOUBT 1 WHILE SOLVING
/*WHY CANT WE RETURN userRepo.findAll() bcz it only works for the User butt we want to return
UserResponse thats why we need to map that and convert it to ToResponse*/
