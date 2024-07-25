package com.example.want.api.block.domain;

import javax.persistence.*;

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @Column(name = "state_id", insertable = false, updatable = false)
    private Integer stateId;

    // 다른 필요한 필드와 getter, setter 추가
}
