package com.example.want.api.location.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
//    블록에 추가되면 로케이션에 갱신
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;
    private Long popularCount;

    public void popularCount(Long popularCount) {
        Location location = Location.builder()
                .popularCount(popularCount)
                .build();
    }
}
