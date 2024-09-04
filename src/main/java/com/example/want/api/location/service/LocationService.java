package com.example.want.api.location.service;

import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.location.domain.Location;
import com.example.want.api.location.dto.LocationResDto;
import com.example.want.api.location.repository.LocationRepository;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final BlockRepository blockRepository;
    private final LocationRepository locationRepository;
    private final ProjectRepository projectRepository;
    private final StateRepository stateRepository;

    @Qualifier("popular")
    private final RedisTemplate<String, Long> popularRedisTemplate;

    @Transactional
    public List<LocationResDto> getPopularLocations(Long stateId) {
        List<Location> findLocation = locationRepository.findAllByStateIdOrderByPopularCountDesc(stateId);
        List<LocationResDto> locations = new ArrayList<>();
        for (Location location : findLocation) {
            LocationResDto dto = location.fromEntity(location);
            locations.add(dto);
        }
        return locations;
    }

    // 스케줄링 (redis -> db)
    @Transactional
    public void getPopularCountFromCache() {
        ValueOperations<String, Long> valueOperations = popularRedisTemplate.opsForValue();

        // Redis 에 저장된 모든 key 를 가져옴.
        Set<String> keys = popularRedisTemplate.keys("*");

        if (keys != null) {
            for (String cacheKey : keys) {
                Long cacheValue = valueOperations.get(cacheKey);
                if (cacheValue != null) {
                    // key는 "위도:경도" 형식. 이를 분리.
                    String[] findKey = cacheKey.split(":");

                    Double latitude = Double.parseDouble(findKey[0]);
                    Double longitude = Double.parseDouble(findKey[1]);

                    // 해당 위치를 LocationRepository 에서 찾기
                    Location location = locationRepository.findByLatitudeAndLongitude(latitude, longitude);
                    if (location == null) {
                        Location newLocation = Location.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .popularCount(cacheValue)
                                .build();
                        locationRepository.save(newLocation);
                    }
                    else{
                        location.updatePopularCount(cacheValue);
                    }
                    popularRedisTemplate.delete(cacheKey);
                }

            }

        }
    }

}