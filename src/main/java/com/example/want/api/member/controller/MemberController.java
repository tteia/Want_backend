package com.example.want.api.member.controller;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.dto.AcceptInvitationDto;
import com.example.want.api.member.dto.GetInvitationDto;
import com.example.want.api.member.login.UserInfo;
import com.example.want.api.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "예제 API", description = "Swagger 테스트용 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserInfo userInfo) {
        Member member = memberService.getMyProfile(userInfo.getEmail());
        return ResponseEntity.ok(member);
    }

//    초대 요청 목록 확인
    @GetMapping("/invitations")
    public ResponseEntity<?> getInvitations(@AuthenticationPrincipal UserInfo userInfo) {
        List<GetInvitationDto> invitations = memberService.getMyInvitations(userInfo.getEmail());
        return ResponseEntity.ok(invitations);
    }

//    초대 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<String> acceptInvitation (@RequestBody AcceptInvitationDto dto,
                                                    @AuthenticationPrincipal UserInfo userInfo) {
        String email = userInfo.getEmail();
        memberService.acceptInvitation(email, dto.getProjectId());
        return new ResponseEntity<>("Invitation accepted successfully.", HttpStatus.OK);
    }
}