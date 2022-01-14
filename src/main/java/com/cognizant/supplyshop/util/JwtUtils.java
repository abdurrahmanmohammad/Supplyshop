package com.cognizant.supplyshop.util;


import com.cognizant.supplyshop.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class JwtUtils {
    private static final String SECRET_KEY = "secret";

    public String generateToken(User userInfo){
        return createToken(new HashMap<String, Object>(), userInfo.getEmail());
    }

    public Boolean validateToken(String token, User userInfo){
        return !isTokenExpired(token) && extractEmail(token).equals(userInfo.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject){
        Date now = new Date(System.currentTimeMillis());
        Date until = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(now).setExpiration(until).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
}
