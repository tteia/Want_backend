package com.example.want.api.sse;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.login.UserInfo;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.projectMember.Repository.ProjectMemberRepository;
import com.example.want.api.projectMember.domain.ProjectMember;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.persistence.EntityNotFoundException;

@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    public NotificationController(NotificationService notificationService, ProjectMemberRepository projectMemberRepository, MemberRepository memberRepository, ProjectRepository projectRepository) {
        this.notificationService = notificationService;
        this.projectMemberRepository = projectMemberRepository;
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/api/notifications/{projectId}")
    public SseEmitter streamNotifications(@PathVariable Long projectId, @AuthenticationPrincipal UserInfo userInfo ) {
        // 멤버가 해당 프로젝트에 속해 있는지 확인
        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        ProjectMember projectMember = projectMemberRepository.findByProjectAndMember(project, member)
                .orElseThrow(() -> new EntityNotFoundException("ProjectMember not found"));

        if (projectMember != null && "Y".equals(projectMember.getIsExist())) {
            return notificationService.addEmitter(projectId, member.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Member is not part of this project");
        }
    }

    @GetMapping(value = "/api/subscribe", produces = "text/event-stream")
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserInfo userInfo) {
        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        System.out.println("구독 좋아요 알림설정 : " + member.getEmail());
        return notificationService.subscribeEmitter(member.getEmail());
    }

}
