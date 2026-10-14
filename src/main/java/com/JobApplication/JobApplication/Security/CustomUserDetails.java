package com.JobApplication.JobApplication.Security;

import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Enum.Role;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//This is where we connect your existing User entity with Spring Security.
//Gives the information in User Entity in such understandable format to Spring Security

public class CustomUserDetails implements UserDetails
{
    private final User user;

    public CustomUserDetails(User user)
    {
        this.user = user;

    }

    //ADDED THIS TWO GETTERS FOR JWT SERVICE
    public Long getId()
    {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public @Nullable String getPassword()    //return the password which is basically encrypted
    {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }


}

//Ha class user register karat nahi ani database madhye save pan karat nahi.
// To Spring Security la user chi details ani permissions samjavto.