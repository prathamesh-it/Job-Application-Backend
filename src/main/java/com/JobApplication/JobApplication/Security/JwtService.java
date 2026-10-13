package com.JobApplication.JobApplication.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.util.Date;

@Service
public class JwtService
{
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(CustomUserDetails user)
    {
        return Jwts.builder()
                .subject(user.getUsername())   //email
                .claim("userId" , user.getID())
                .claim("role" , user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }


    //THROW THE JWTEXCEPTION IF TOKE IS FAKE , TAMEPERED OR EXPIRED
    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token)
    {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token , UserDetails userDetails)
    {
        Claims claims = extractAllClaims(token);  //SIGNATURE + EXPIRY CHECK
        return claims.getSubject().equals(userDetails.getUsername());
    }
}
