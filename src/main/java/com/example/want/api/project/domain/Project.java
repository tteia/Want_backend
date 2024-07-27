package com.example.want.api.project.domain;

import com.example.want.api.traveluser.domain.TravelUser;
import com.example.want.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Setter
    private List<TravelUser> travelUsers = new ArrayList<>();

}
