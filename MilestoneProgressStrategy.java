package com.example.construction.service;

import com.example.construction.entity.Milestone;
import com.example.construction.repository.MilestoneRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MilestoneProgressStrategy implements ProgressCalculationStrategy {

    private final MilestoneRepository milestoneRepository;

    public MilestoneProgressStrategy(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    @Override
    public double calculateProgress(Integer projectId) {

        List<Milestone> milestones =
                milestoneRepository.findByProjectId(projectId);

        if (milestones.isEmpty()) {
            return 0.0;
        }

        long completedMilestones = milestones.stream()
                .filter(milestone ->
                        "COMPLETED".equalsIgnoreCase(milestone.getStatus()))
                .count();

        return (completedMilestones * 100.0) / milestones.size();
    }
}