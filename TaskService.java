package com.example.construction.service;

import com.example.construction.entity.Task;
import com.example.construction.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByProject(Integer projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }
}