package com.example.want.api.schedule.controller;

import com.example.want.api.schedule.domain.Schedule;
import com.example.want.api.schedule.dto.ScheduleCreateReqDto;
import com.example.want.api.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{date}")
    public List<Schedule> getSchedulesByDate(@RequestBody String date) {
        LocalDate localDate = LocalDate.parse(date); // date 찾아서 파싱함.
        return scheduleService.getSchedulesByDate(localDate);
    }

    @PostMapping("/{date}")
    public Schedule postScheduleByDate(@RequestBody ScheduleCreateReqDto scheduleCreateReqDto) {
        return scheduleService.createSchedule(scheduleCreateReqDto);
    }
}
