package com.example.want.api.location.dto;


import com.example.want.api.location.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationReqDto {
    private Double latitude;
    private Double longitude;

    public static LocationReqDto toEntity(Location location) {
        LocationReqDto dto = LocationReqDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        return dto;
    }



}