package com.example.construction.service;

import com.example.construction.entity.Task;
import com.example.construction.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskProgressStrategy implements ProgressCalculationStrategy {

    private final TaskRepository taskRepository;

    public TaskProgressStrategy(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public double calculateProgress(Integer projectId) {

        List<Task> tasks = taskRepository.findByProjectId(projectId);

        if (tasks.isEmpty()) {
            return 0.0;
        }

        long completedTasks = tasks.stream()
                .filter(task -> "COMPLETED".equalsIgnoreCase(task.getStatus()))
                .count();

        return (completedTasks * 100.0) / tasks.size();
    }
}