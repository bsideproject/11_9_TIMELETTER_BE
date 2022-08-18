package com.timeletter.api.security;

import com.timeletter.api.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "Ns43fsd233MM9F05fNs43fsd233MM9F05fNs43fsd233MM9F05fNs43fsd233MM9F05f";
    private static final int amountToAdd = 100;
    private static final TemporalUnit unit = ChronoUnit.DAYS;

    /**
     * 토큰 생성
     *
     * @param member 사용자 정보
     * @return 토큰 발행한 것
     */
    public String create(Member member){
        Date expiryDate = getExpiryDate();
        Key key = getKey();

        return Jwts.builder()
                .signWith(key)
                .setSubject(member.getEmail())
                .setIssuer("TimeLetter api")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }

    /**
     * 토큰 만료 일자 설정하기
     *
     * @return 토큰 만료 일자
     */
    private Date getExpiryDate() {
        return Date.from(Instant.now().plus(amountToAdd, unit));
    }

    /**
     * 토큰정보를 바탕으로 이메일을 반환한다.
     *
     * @param token 토큰 정보
     * @return 이메일
     */
    public String validateAndGetUserID(String token){
        Key key = getKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 키를 가져온다.
     *
     * @return 시크릿 키를 암호화한 값
     */
    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }
}
