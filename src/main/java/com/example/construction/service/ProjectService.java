package com.example.construction.service;

import com.example.construction.entity.Project;
import com.example.construction.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Integer id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Integer id, Project updatedProject) {
        return projectRepository.findById(id)
                .map(existingProject -> {
                    existingProject.setProjectName(updatedProject.getProjectName());
                    existingProject.setDescription(updatedProject.getDescription());
                    existingProject.setLocation(updatedProject.getLocation());
                    existingProject.setStartDate(updatedProject.getStartDate());
                    existingProject.setEndDate(updatedProject.getEndDate());
                    existingProject.setStatus(updatedProject.getStatus());
                    existingProject.setClientId(updatedProject.getClientId());
                    existingProject.setContractorId(updatedProject.getContractorId());

                    return projectRepository.save(existingProject);
                })
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void deleteProject(Integer id) {
        projectRepository.deleteById(id);
    }
}