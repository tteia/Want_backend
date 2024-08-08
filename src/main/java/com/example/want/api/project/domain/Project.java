package com.example.want.api.project.domain;

import com.example.want.api.projectMember.domain.ProjectMember;
import com.example.want.api.state.domain.ProjectState;
import com.example.want.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

//    일정 삭제 여부
    private String isDeleted;
    
//    일정 종료 여부
    private String isDone;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<ProjectState> projectStates = new ArrayList<>();

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = "Y";
    }

//    엔티티가 생성 될 때 마다 실행되어야 하는 로직
    @PrePersist
    public void initializeFields() {
        this.createdAt = LocalDateTime.now();
        if(this.isDeleted == null) {
            this.isDeleted = "N";
        }
        if(this.isDone == null) {
            this.isDone = "N";
        }
    }

    public void updateIsDone(String isDone) {
        this.isDone = isDone;
    }
}
