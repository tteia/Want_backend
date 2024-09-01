package com.example.want.api.location.controller;

import com.example.want.api.location.dto.LocationReqDto;
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

    @PostMapping("/popular/increment")
    public ResponseEntity<CommonResDto> incrementPopularCount(@RequestBody LocationReqDto locationDto) {
        locationService.addPopularCount(locationDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "인기도 카운트 완료", locationDto.getPopularCount());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/popular/decrement")
    public ResponseEntity<CommonResDto> decrementPopularCount(@RequestBody LocationReqDto locationReqDto) {
        locationService.removePopularCount(locationReqDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "인기도 카운트 완료", locationReqDto.getPopularCount());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<CommonResDto> getPopularLocations() {
        List<LocationReqDto> popularLocations = locationService.getPopularFromDb();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 완료", popularLocations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}