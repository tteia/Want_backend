package com.example.want.api.project.service;

import com.example.want.api.project.domain.Project;
import com.example.want.api.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ProjectScheduler {
    @Autowired
    private final ProjectRepository projectRepository;

    public ProjectScheduler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

//    매일 0시 0분 0초
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void projectSchedule() {
        List<Project> projects = projectRepository.findByIsDeleted("N");
        for (Project p : projects) {
            if (p.getEndTravel().isAfter(LocalDate.now()) && p.getIsDone().equals("N")) {
                p.updateIsDone("Y");
            }
        }
    }
}
