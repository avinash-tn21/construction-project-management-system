package com.example.construction.repository;

import com.example.construction.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByProjectId(Integer projectId);
}