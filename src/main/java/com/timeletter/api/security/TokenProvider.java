package com.timeletter.api.security;

import com.timeletter.api.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "Ns43fsd233MM9F05f";

    public String create(Member member){
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder().signWith(key,SignatureAlgorithm.HS512)
                .setSubject(member.getId())
                .setIssuer("TimeLetter api")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }

    public String validateAndGetUserID(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
