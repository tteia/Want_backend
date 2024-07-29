package com.example.want.api.project.domain;

import com.example.want.api.projectMember.domain.ProjectMember;
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

    private String isDeleted;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<ProjectMember> projectMembers = new ArrayList<>();

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = "Y";
    }

//    엔티티가 생성 될 때 마다 실행되어야 하는 로직
    @PrePersist
    public void initializeFields() {
        this.createdAt = LocalDateTime.now();
        this.isDeleted = "N";
    }

}
