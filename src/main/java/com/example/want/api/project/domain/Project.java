package com.example.want.api.project.domain;

import com.example.want.api.project.dto.ProjectResDto;
import com.example.want.api.project.dto.ProjectUpdateDto;
import com.example.want.api.traveluser.domain.TravelGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<TravelGroup> travelGroups;

//    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
//    private List<Block> blocks;

//     여행지
//     @OneToMany(mappedBy = "stateTravel")
//     private StateTravel stateTravel;

    public void updateProject(ProjectUpdateDto dto) {
        this.title = dto.getTitle();
        this.startTravel = dto.getStartTravel();
        this.endTravel = dto.getEndTravel();
//        그룹을 변경하는 일?
//        this.stateTravel = dto.getStateTravel();
//        그룹 및 여행지 업데이트 로직 추가 필요 시 추가
    }

    public ProjectResDto listFromEntity() {
        ProjectResDto projectResDto = ProjectResDto.builder()
                .id(this.id)
                .title(this.title)
                .startTravel(this.startTravel)
                .endTravel(this.endTravel)
                .createdAt(this.createdAt)
                .build();
        return projectResDto;
    }

    public ProjectResDto detFromEntity() {
        return ProjectResDto.builder()
                .id(id)
                .title(title)
                .startTravel(startTravel)
                .endTravel(endTravel)
                .createdAt(createdAt)
//                .memberId()
//                .stateTravel()
                .build();
    }
}
