package com.example.want.api.projectMember.domain;

import com.example.want.api.member.domain.Member;
import com.example.want.api.project.domain.Authority;
import com.example.want.api.project.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1)
    private String invitationAccepted;

    private String invitationCode;

    private String isExist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    public void updateIsExist(String isExist) {
        this.isExist = isExist;
    }
}