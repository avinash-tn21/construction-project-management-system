package com.example.construction.service;

import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    private ProgressCalculationStrategy strategy;

    public ProgressService(TaskProgressStrategy taskProgressStrategy) {
        this.strategy = taskProgressStrategy;
    }

    public double calculateProgress(Integer projectId) {
        return strategy.calculateProgress(projectId);
    }

    public void setStrategy(ProgressCalculationStrategy strategy) {
        this.strategy = strategy;
    }
}