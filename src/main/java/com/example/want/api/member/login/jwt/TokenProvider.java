package com.example.want.api.member.login.jwt;

import com.example.want.api.member.login.UserInfo;
import com.example.want.api.member.domain.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 10; // 10시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    private final Key key;

    @Autowired
    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponse generateJwtToken(String email, String name, String profileUrl, Role role) {
        long now = (new Date()).getTime();

        Map<String, Object> payloads = Map.of(
                "email", email,
                "name", name,
                "profileUrl", profileUrl,
                AUTHORITIES_KEY, role.toString() // ROLE_USER 같은 문자열로 저장
        );

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setClaims(payloads)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(payloads)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new TokenResponse(BEARER_TYPE, accessToken, refreshToken, accessTokenExpiresIn.getTime());
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new UserInfo(
                claims.get("email") != null ? claims.get("email").toString() : "unknown",
                claims.get("name") != null ? claims.get("name").toString() : "unknown",
                authorities
        );
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // validateToken 메소드는 토큰의 유효성을 검증하는 메소드입니다.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.info(token + " 잘못된 JWT 서명입니다.");
            throw new TokenNotValidateException("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            logger.warn("만료된 JWT 토큰입니다.");
            throw new TokenNotValidateException("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            logger.warn("지원되지 않는 JWT 토큰입니다.");
            throw new TokenNotValidateException("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            logger.warn("JWT 토큰이 잘못되었습니다.");
            throw new TokenNotValidateException("JWT 토큰이 잘못되었습니다.", e);
        }
    }

    // parseClaims 메소드는 토큰을 파싱하여 클레임을 추출하는 메소드입니다.
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            logger.error("토큰 파싱 중 오류 발생", e);
            throw new TokenNotValidateException("토큰 파싱 중 오류 발생", e);
        }
    }

    public class TokenNotValidateException extends JwtException {
        public TokenNotValidateException(String message) {
            super(message);
        }

        public TokenNotValidateException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
