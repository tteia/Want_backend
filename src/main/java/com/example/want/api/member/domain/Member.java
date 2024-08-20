package com.example.want.api.member.domain;

import com.example.want.api.member.login.oauth.GoogleOAuth2UserInfo;
import com.example.want.api.projectMember.domain.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String profileUrl;
    @Enumerated(EnumType.STRING)
    private Role role;

    public Member(GoogleOAuth2UserInfo userInfo) {
        this.name = userInfo.getName();
        this.email = userInfo.getEmail();
        this.profileUrl = userInfo.getProfileImageUrl();
        this.role = Role.MEMBER;
    }
}
