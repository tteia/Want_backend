package com.example.want.api.state.repository;

import com.example.want.api.state.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findAllByCityIsNull();

    List<State> findAllByCountryAndCityIsNotNull(String countryName);
}
