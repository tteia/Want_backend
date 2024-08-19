package com.example.want.api.state.controller;

import com.example.want.api.state.domain.State;
import com.example.want.api.state.dto.CityResDto;
import com.example.want.api.state.dto.CountryRsDto;
import com.example.want.api.state.service.StateService;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StateController {
    private final StateService stateService;

    @GetMapping("/state/country")
    public ResponseEntity<?> getCountry(){
        List<CountryRsDto> country = stateService.getCountryList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", country);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/popular/destinations")
    public ResponseEntity<?> getCitiesWithProjects() {
        List<CityResDto> cities = stateService.getCitiesWithProjectCounts();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", cities);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/state/city")
    public ResponseEntity<?> getCity(@RequestParam String countryName){
        List<CityResDto> city = stateService.getCityList(countryName);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", city);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
