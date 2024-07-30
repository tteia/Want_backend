package com.example.want.api.state.dto;

import com.example.want.api.state.domain.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityResDto {
    private Long id;
    private String city;


    public static CityResDto fromEntity(State state) {
        return CityResDto.builder()
            .id(state.getId())
            .city(state.getCity())
            .build();
    }
}
