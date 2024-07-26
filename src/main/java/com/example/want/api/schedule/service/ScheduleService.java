package com.example.want.api.schedule.service;

import com.example.want.api.schedule.domain.Schedule;
import com.example.want.api.schedule.dto.ScheduleCreateReqDto;
import com.example.want.api.schedule.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByDateOrderByStartTimeAsc(date);
    }

    public Schedule createSchedule(ScheduleCreateReqDto scheduleCreateReqDto) {
        Schedule schedule = Schedule.builder()
                .date(scheduleCreateReqDto.getDate())
                .startTime(scheduleCreateReqDto.getStartTime())
                .endTime(scheduleCreateReqDto.getEndTime())
                .description(scheduleCreateReqDto.getDescription())
                .build();
        return scheduleRepository.save(schedule);
    }
}

