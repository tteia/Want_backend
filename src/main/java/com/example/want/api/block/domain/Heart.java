package com.example.want.placeBlock.domain;

import javax.persistence.*;

@Entity
@Table(name = "heart")
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "block_id", referencedColumnName = "block_id"),
            @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    })
    private Block block;

    @Column(nullable = false)
    private Long userId;

    private int heartCount;
}
