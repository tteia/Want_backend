package com.example.want.api.member.dto;

import com.example.want.api.member.domain.Role;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateRqDto {
    private String name;
    private String email;
    private String password;
    private String type;
    private String profileUrl;
    private Role role;
}
