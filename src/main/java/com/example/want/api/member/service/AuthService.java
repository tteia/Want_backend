package com.example.want.api.member.service;

import com.example.want.api.member.dto.GoogleAccessTokenResponse;
import com.example.want.api.member.dto.RefreshDto;
import com.example.want.api.member.login.dto.GoogleLoginRqDto;
import com.example.want.api.member.login.jwt.TokenProvider;
import com.example.want.api.member.login.jwt.TokenResponse;
import com.example.want.api.member.login.oauth.GoogleOAuth2UserInfo;
import com.example.want.api.member.domain.Member;
import com.example.want.api.member.domain.Role;
import com.example.want.api.member.repository.MemberRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;


import com.fasterxml.jackson.databind.ObjectMapper; // Jackson 라이브러리 추가

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${local.oauth2.google.redirect-uri}")
    private String redirectUri;

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> loginRedisTemplate;
    private final RestTemplate restTemplate = new RestTemplate(); // RestTemplate 인스턴스
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 인스턴스 추가

    @Transactional
    public TokenResponse googleLogin(GoogleLoginRqDto request) {
        try {
            String code = request.getCode();
            GoogleAccessTokenResponse tokenResponse = getAccessTokenFromGoogle(code);
            GoogleIdToken googleIdToken = verifyGoogleIdToken(tokenResponse.getIdToken());

            Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String profileUrl = (String) payload.get("picture");

            Member member = memberRepository.findByEmail(email)
                    .orElseGet(() -> memberRepository.save(new Member(email, name, profileUrl)));

            TokenResponse response = createJwtTokens(email, name, profileUrl);
            storeRefreshTokenInRedis(email, response.getRefreshToken());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error during Google login", e);
        }
    }

    private GoogleAccessTokenResponse getAccessTokenFromGoogle(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirectUri); // 프론트엔드에서 설정한 리디렉션 URI
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // 응답 문자열을 GoogleAccessTokenResponse 객체로 변환
                return objectMapper.readValue(response.getBody(), GoogleAccessTokenResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse access token response", e);
            }
        } else {
            throw new RuntimeException("Failed to get access token from Google. Response: " + response.getBody());
        }
    }

    private GoogleIdToken verifyGoogleIdToken(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Invalid ID token.");
        }
        return googleIdToken;
    }

    private TokenResponse createJwtTokens(String email, String name, String profileUrl) {
        return tokenProvider.generateJwtToken(email, name, profileUrl, Role.MEMBER);
    }

    private void storeRefreshTokenInRedis(String email, String refreshToken) {
        try {
            loginRedisTemplate.opsForValue().set(email, refreshToken, 240, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error saving token to Redis", e);
        }
    }

    public String refreshToken(RefreshDto dto) {
        String refreshToken = dto.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token.");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String storedRefreshToken = (String) loginRedisTemplate.opsForValue().get(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new RuntimeException("Invalid refresh token.");
        }

        TokenResponse tokenResponse = tokenProvider.generateJwtToken(email, member.getName(), member.getProfileUrl(), Role.MEMBER);
        return tokenResponse.getAccessToken();
    }
}




//    private String randomNickname() {
//        final String[] adjectives = {
//                "친절한", "잘생긴", "똑똑한", "용감한", "우아한", "행복한"
//        };
//
//        final String[] nouns = {
//                "고양이", "강아지", "호랑이", "사자", "펭귄", "악어"
//        };
//
//        Random random = new Random();
//        String adjective = adjectives[random.nextInt(adjectives.length)];
//        String noun = nouns[random.nextInt(nouns.length)];
//        int number;
//
//        // 중복되지 않는 닉네임을 생성하기 위해 중복 체크
//        String nickname;
//        do {
//            number = random.nextInt(1000);
//            nickname = adjective + noun + "#" + number;
//        } while (memberRepository.existsByNickname(nickname));
//
//        return nickname;
//    }

//    @Transactional
//    public TokenResponse reissue(String email, String name, String refreshToken) {
//        validateRefreshToken(refreshToken);
//        message = UserResponseMessage.REISSUE_SUCCESS.getMessage();
//        return createToken(email, name );
//    }



//    private void validateRefreshToken(String refreshToken){
//        if(!tokenProvider.validateToken(refreshToken))
//            throw new ApiException(UserResponseMessage.REFRESH_TOKEN_INVALID);
//    }




//    @Transactional
//    public void withdrawUser(String email){
//        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() ->
//                new ApiException(UserResponseMessage.USER_NOT_FOUND));
//
//        userEntity.setWithdrawalDate(LocalDateTime.now());
//        userRepository.save(userEntity);
//    }

