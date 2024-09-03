package com.example.want.api.location.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.dto.BlockActiveListRsDto;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.location.domain.Location;
import com.example.want.api.location.dto.LocationReqDto;
import com.example.want.api.location.dto.LocationResDto;
import com.example.want.api.location.repository.LocationRepository;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.state.domain.State;
import com.example.want.api.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    private void getPopularCountFromCache(String key, Long locationId) {
        ValueOperations<String, Long> valueOperations = popularRedisTemplate.opsForValue();
        Long cachedValue = valueOperations.get(key);
        // key 찾아서 해당 key 가 location 에 있으면 그 location 의 popularCount 값 증가
        // 없으면 해당 value 로 popularCount 값 세팅
        if (cachedValue != null) {
            Location location = locationRepository.findById(locationId).orElseThrow(() -> new EntityNotFoundException("해당 Location 을 찾을 수 없습니다."));
            location.updatePopularCount(cachedValue);
            locationRepository.save(location);
        }

    }




}