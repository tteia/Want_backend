package com.example.want.api.member.controller;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.dto.AcceptInvitationDto;
import com.example.want.api.member.dto.InvitationResDto;
import com.example.want.api.member.login.UserInfo;
import com.example.want.api.member.service.MemberService;
import com.example.want.common.CommonResDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    @Transactional
    @GetMapping("/invitations")
    public ResponseEntity<?> getInvitations(@AuthenticationPrincipal UserInfo userInfo,
                                            @PageableDefault(size = 10) Pageable pageable) {
        Page<InvitationResDto> invitations = memberService.getMyInvitations(userInfo.getEmail(), pageable);
        return ResponseEntity.ok(invitations);
    }

    //    초대 요청 처리 (수락, 거절)
    @Transactional
    @PostMapping("/invitations/response")
    public ResponseEntity<?> responseInvitation(@RequestBody AcceptInvitationDto dto, @AuthenticationPrincipal UserInfo userInfo, @PageableDefault(size = 10) Pageable pageable) {
        String email = userInfo.getEmail();
        memberService.invitationAcceptOrReject(email, dto);
//        잔여 초대 목록을 확인하기 위함.
        Page<InvitationResDto> updatedInvitations = memberService.getMyInvitations(email, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Member response successfully.", updatedInvitations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}