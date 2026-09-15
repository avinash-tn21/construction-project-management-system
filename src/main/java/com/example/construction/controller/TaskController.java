package com.example.construction.controller;

import com.example.construction.entity.Task;
import com.example.construction.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Integer projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {

        if (task.getProjectId() == null) {
            return ResponseEntity.badRequest()
                    .body("Project is required.");
        }

        if (task.getTaskName() == null || task.getTaskName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Task name is required.");
        }

        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("PENDING");
        }

        if (task.getStartDate() != null && task.getEndDate() != null
                && task.getEndDate().isBefore(task.getStartDate())) {
            return ResponseEntity.badRequest()
                    .body("End date cannot be before start date.");
        }

        if (task.getEstimatedDuration() != null
                && task.getEstimatedDuration() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Estimated duration must be greater than 0.");
        }

        return ResponseEntity.ok(taskService.saveTask(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Integer id,
            @RequestBody Task taskDetails) {

        return taskService.getTaskById(id)
                .map(task -> {

                    if (taskDetails.getTaskName() == null
                            || taskDetails.getTaskName().isBlank()) {
                        return ResponseEntity.badRequest().body("Task name is required.");
                    }

                    if (taskDetails.getStartDate() != null
                            && taskDetails.getEndDate() != null
                            && taskDetails.getEndDate().isBefore(taskDetails.getStartDate())) {
                        return ResponseEntity.badRequest()
                                .body("End date cannot be before start date.");
                    }

                    if (taskDetails.getEstimatedDuration() != null
                            && taskDetails.getEstimatedDuration() <= 0) {
                        return ResponseEntity.badRequest()
                                .body("Estimated duration must be greater than 0.");
                    }

                    task.setTaskName(taskDetails.getTaskName());
                    task.setDescription(taskDetails.getDescription());
                    task.setPhase(taskDetails.getPhase());
                    task.setStartDate(taskDetails.getStartDate());
                    task.setEndDate(taskDetails.getEndDate());
                    task.setEstimatedDuration(taskDetails.getEstimatedDuration());
                    task.setDependsOnTaskId(taskDetails.getDependsOnTaskId());
                    task.setStatus(taskDetails.getStatus());
                    task.setProjectId(taskDetails.getProjectId());

                    return ResponseEntity.ok(taskService.saveTask(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id) {

        if (taskService.getTaskById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}