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
public class CountryRsDto {
    private String country;

    public static CountryRsDto fromEntity(State state) {
        return CountryRsDto.builder()
            .country(state.getCountry())
            .build();
    }
}
