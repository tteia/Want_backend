package com.example.want.api.traveluser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaderDto {
//    일단 id만 설정 해놨는데 이름, email도 넣어놔야 하는지
    private Long leaderId;
}
