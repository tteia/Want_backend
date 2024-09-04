package com.example.want.api.member.controller;

import com.example.want.api.member.dto.RefreshDto;
import com.example.want.api.member.login.dto.GoogleLoginRqDto;
import com.example.want.api.member.service.AuthService;
import com.example.want.api.member.login.jwt.TokenResponse;
import com.example.want.common.CommonResDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRqDto request) throws Exception {
        TokenResponse tokenResponse = authService.googleLogin(request);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", tokenResponse);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshDto dto) {
        String newAt = authService.refreshToken(dto);
        Map<String, Object> info = new HashMap<>();
        info.put("token", newAt);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", info);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}

//    로그아웃
//    @PostMapping("/token")
//    public ResponseEntity<?> reissue(@AuthenticationPrincipal UserInfo user, @RequestBody LogoutRequest logoutRequest) throws Exception {
//        TokenResponse tokenResponse = authService.reissue(user.getEmail(),user.getName(), logoutRequest.refreshToken());
//        return ResponseEntity.ok(tokenResponse);
//    }

//    회원탈퇴
//    @DeleteMapping("/auth/withdraw")
//    public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal UserInfo user) {
//        authService.withdrawUser(user.getEmail());
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", null);
//        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
//    }

