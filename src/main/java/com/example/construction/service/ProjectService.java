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

    public List<Project> getProjectsByClient(Integer clientId) {
        return projectRepository.findByClientId(clientId);
    }

    public Project createProject(Project project) {
        validateProjectDates(project);
        validateStatus(project);
        return projectRepository.save(project);
    }

    public Project updateProject(Integer id, Project updatedProject) {

        validateProjectDates(updatedProject);
        validateStatus(updatedProject);

        return projectRepository.findById(id)
                .map(existingProject -> {

                    existingProject.setProjectName(
                            updatedProject.getProjectName());

                    existingProject.setDescription(
                            updatedProject.getDescription());

                    existingProject.setLocation(
                            updatedProject.getLocation());

                    existingProject.setStartDate(
                            updatedProject.getStartDate());

                    existingProject.setEndDate(
                            updatedProject.getEndDate());

                    existingProject.setStatus(
                            updatedProject.getStatus());

                    existingProject.setClientId(
                            updatedProject.getClientId());

                    existingProject.setContractorId(
                            updatedProject.getContractorId());

                    return projectRepository.save(existingProject);
                })
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));
    }

    private void validateProjectDates(Project project) {

        if (project.getStartDate() != null &&
                project.getEndDate() != null &&
                project.getEndDate()
                        .isBefore(project.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }
    }

    private void validateStatus(Project project) {

        if (project.getStatus() == null) {
            return;
        }

        String status = project.getStatus().trim().toUpperCase();

        if (!status.equals("PENDING") &&
                !status.equals("IN PROGRESS") &&
                !status.equals("COMPLETED") &&
                !status.equals("ON HOLD") &&
                !status.equals("CANCELLED")) {

            throw new IllegalArgumentException(
                    "Invalid project status.");
        }
    }

    public void deleteProject(Integer id) {
        projectRepository.deleteById(id);
    }
}
