package com.example.want.api.location.controller;

import com.example.want.api.location.domain.Location;
import com.example.want.api.location.dto.LocationResDto;
import com.example.want.api.location.repository.LocationRepository;
import com.example.want.api.location.service.LocationService;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LocationController {

    private final LocationService locationService;
    private final LocationRepository locationRepository;

    // 추천 블럭 리스트에 사용할 코드
    @GetMapping("/city/{stateId}")
    public ResponseEntity<CommonResDto> getPopularLocations(@PathVariable Long stateId) {
        List<LocationResDto> popularLocations = locationService.getPopularLocations(stateId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 완료", popularLocations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/pop")
    public ResponseEntity<CommonResDto> getPopularCountFromCache() {
        locationService.getPopularCountFromCache();
        List<Location> popularLocations = locationRepository.findAll();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 완료", popularLocations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}