package com.JobApplication.JobApplication.Security;


//This runs once per request.
// It reads Authorization: Bearer <token>, validates it, and tells Spring Security who the caller is.

import com.JobApplication.JobApplication.Exceptions.UserNotFoundException;
import com.JobApplication.JobApplication.Service.MyUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);  //no token , continue unauthenticated
            return;
        }

        String token = header.substring(7);  //remove "Bearer "

        try {
            String email = jwtService.extractEmail(token);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if(jwtService.isTokenValid(token , userDetails))
                {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails , null ,
                                    userDetails.getAuthorities()
                            );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        catch (JwtException | UsernameNotFoundException | UserNotFoundException ex)
        {
            //bad token or deleted user : leave unauthenticated , security return 401
        }
        filterChain.doFilter(request,response);
    }




}
//This runs once per request.
// It reads Authorization: Bearer <token>, validates it, and tells Spring Security who the caller is.
