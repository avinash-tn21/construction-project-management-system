package com.example.construction.controller;

import com.example.construction.service.MilestoneProgressStrategy;
import com.example.construction.service.ProgressService;
import com.example.construction.service.TaskProgressStrategy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final TaskProgressStrategy taskProgressStrategy;
    private final MilestoneProgressStrategy milestoneProgressStrategy;

    public ProgressController(
            ProgressService progressService,
            TaskProgressStrategy taskProgressStrategy,
            MilestoneProgressStrategy milestoneProgressStrategy) {

        this.progressService = progressService;
        this.taskProgressStrategy = taskProgressStrategy;
        this.milestoneProgressStrategy = milestoneProgressStrategy;
    }

    @GetMapping("/project/{projectId}/tasks")
    public double getTaskProgress(@PathVariable Integer projectId) {

        progressService.setStrategy(taskProgressStrategy);

        return progressService.calculateProgress(projectId);
    }

    @GetMapping("/project/{projectId}/milestones")
    public double getMilestoneProgress(@PathVariable Integer projectId) {

        progressService.setStrategy(milestoneProgressStrategy);

        return progressService.calculateProgress(projectId);
    }
}