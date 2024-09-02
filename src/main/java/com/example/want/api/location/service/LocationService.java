package com.example.want.api.location.service;

import com.example.want.api.location.domain.Location;
import com.example.want.api.location.dto.LocationReqDto;
import com.example.want.api.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;

    @Qualifier("popular")
    private final RedisTemplate<String, Object> popularRedisTemplate;

    public Location addPopularCount(LocationReqDto locationReqDto) {
        String redisKey = locationReqDto.getLatitude() + ":" + locationReqDto.getLongitude();

        // 레디스에서 해당 위치의 popularCount +1
        Long popularCount = popularRedisTemplate.opsForValue().increment(redisKey);

        // DB에서도 업데이트 (레디스와의 싱크를 맞추기 위해)
        Location location = locationRepository.findByLatitudeAndLongitude(locationReqDto.getLatitude(), locationReqDto.getLongitude());
        location.popularCount(popularCount);
        locationRepository.save(location);
        return location;
    }

    public void removePopularCount(LocationReqDto locationReqDto) {
        String redisKey = locationReqDto.getLatitude() + ":" + locationReqDto.getLongitude();

        // 레디스에서 해당 위치의 popularCount -1
        Long popularCount = popularRedisTemplate.opsForValue().decrement(redisKey);

        // DB에서도 업데이트 (레디스와의 싱크를 맞추기 위해)
        Location location = locationRepository.findByLatitudeAndLongitude(locationReqDto.getLatitude(), locationReqDto.getLongitude());
        location.popularCount(popularCount);
        locationRepository.save(location);
    }

    public List<LocationReqDto> getPopularFromDb() {
        List<Location> locations = locationRepository.findAllByOrderByPopularCountDesc();
        List<LocationReqDto> locationReqDtos = new ArrayList<>();
        for (Location location : locations) {
            locationReqDtos.add(LocationReqDto.toEntity(location));
        }
        return locationReqDtos;
    }


}