package com.example.want.api.project.domain;

import com.example.want.api.block.domain.Block;
import com.example.want.api.project.dto.ProjectDetailResDto;
import com.example.want.api.project.dto.ProjectResDto;
import com.example.want.api.project.dto.ProjectUpdateDto;
import com.example.want.api.traveluser.domain.TravelUser;
import com.example.want.api.user.domain.Member;
import com.example.want.common.BaseEntity;
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
@Builder(toBuilder = true)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;

    private LocalDateTime deletedAt;

    private String isDeleted;

    @OneToMany(mappedBy = "project")
    private List<TravelUser> travelUsers;

    @OneToMany(mappedBy = "project")
    private List<Block> blockList;

}
