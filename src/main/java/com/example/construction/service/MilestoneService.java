package com.example.construction.service;

import com.example.construction.entity.Milestone;
import com.example.construction.repository.MilestoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;

    public MilestoneService(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    public List<Milestone> getAllMilestones() {
        return milestoneRepository.findAll();
    }

    public List<Milestone> getMilestonesByProject(Integer projectId) {
        return milestoneRepository.findByProjectId(projectId);
    }

    public Optional<Milestone> getMilestoneById(Integer id) {
        return milestoneRepository.findById(id);
    }

    public Milestone saveMilestone(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }

    public void deleteMilestone(Integer id) {
        milestoneRepository.deleteById(id);
    }
}