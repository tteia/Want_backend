package com.example.want.api.state.repository;

import com.example.want.api.state.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findAllByCityIsNull();

    List<State> findAllByCountryAndCityIsNotNull(String countryName);

    Optional<State> findByCountryAndCity(String country, String city);

    List<State> findAllByCityIsNotNull();

    Long findByProjectId(Long id);

}
