package com.example.want.api.location.repository;

import com.example.want.api.location.domain.Location;
import com.example.want.api.location.dto.LocationResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByLatitudeAndLongitude(Double latitude, Double longitude);
    List<Location> findAllByOrderByPopularCountDesc();
    List<Location> findAllByStateIdOrderByPopularCountDesc(Long stateId);
}