package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Exceptions.UserNotFoundException;
import com.JobApplication.JobApplication.Repository.UserRepository;
import com.JobApplication.JobApplication.Security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService
{
    @Autowired
   private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new CustomUserDetails(user);

    }


}

//Spring Security la email varun user chi details milavun dene.
// Ha class authentication process madhye automatically call hou shakto.
