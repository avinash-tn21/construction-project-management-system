package com.example.construction.controller;

import com.example.construction.entity.Project;
import com.example.construction.service.ClientAccessService;
import com.example.construction.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ClientAccessService clientAccessService;

    public ProjectController(ProjectService projectService,
                             ClientAccessService clientAccessService) {
        this.projectService = projectService;
        this.clientAccessService = clientAccessService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(
            org.springframework.security.core.Authentication authentication) {

        if (isClient(authentication)) {
            return clientAccessService.getCurrentClient(authentication)
                    .map(client -> ResponseEntity.ok(
                            projectService.getProjectsByClient(client.getClientId())))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer id,
            org.springframework.security.core.Authentication authentication) {
        if (isClient(authentication)) {
            return clientAccessService.getOwnedProject(authentication, id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Project createProject(
            @Valid @RequestBody Project project) {

        return projectService.createProject(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Integer id,
            @Valid @RequestBody Project project) {

        try {
            return ResponseEntity.ok(
                    projectService.updateProject(id, project)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Integer id) {

        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isClient(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT"));
    }
}
