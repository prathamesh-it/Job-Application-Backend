package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.UserRequest;
import com.JobApplication.JobApplication.DTOs.UserResponse;
import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Enum.Role;
import com.JobApplication.JobApplication.Exceptions.EmailAlreadyExistException;
import com.JobApplication.JobApplication.Exceptions.UserNotFoundException;
import com.JobApplication.JobApplication.Mappers.UserMapper;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import com.JobApplication.JobApplication.Repository.UserRepository;
import com.JobApplication.JobApplication.Security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService 
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;
    
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
    public UserResponse updateUserById(long id , UserRequest userRequest, CustomUserDetails currentUser)
    {
        if(!currentUser.getId().equals(id))
        {
            throw new AccessDeniedException("You can update only your own account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(!user.getEmail().equals(userRequest.getEmail()) && userRepository.existsByEmail(userRequest.getEmail()))
        {
            throw new EmailAlreadyExistException("Email already exists: " + userRequest.getEmail());
        }

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        return userMapper.toResponse(
                userRepository.save(user)
        );
    }

    @Transactional
    public String deleteUserById(long id, CustomUserDetails currentUser)
    {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if(!isAdmin && !currentUser.getId().equals(id))
        {
            throw new AccessDeniedException("You can delete only your own account");
        }

       if(!userRepository.existsById(id))
       {
           throw new UserNotFoundException("User not found with id: " + id);
       }

        companyRepository.clearOwner(id);   // must come before deleteById
        userRepository.deleteById(id);
        return "User deleted successfully";

    }
}


//DOUBT 1 WHILE SOLVING
/*WHY CANT WE RETURN userRepo.findAll() bcz it only works for the User butt we want to return
UserResponse thats why we need to map that and convert it to ToResponse*/
