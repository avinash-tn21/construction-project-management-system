package com.example.construction.controller;

import com.example.construction.entity.Milestone;
import com.example.construction.service.ClientAccessService;
import com.example.construction.service.MilestoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final ClientAccessService clientAccessService;

    public MilestoneController(MilestoneService milestoneService,
                               ClientAccessService clientAccessService) {
        this.milestoneService = milestoneService;
        this.clientAccessService = clientAccessService;
    }

    @GetMapping
    public ResponseEntity<List<Milestone>> getAllMilestones(
            org.springframework.security.core.Authentication authentication) {
        if (isClient(authentication)) {
            if (clientAccessService.getCurrentClient(authentication).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(milestoneService.getAllMilestones().stream()
                    .filter(milestone -> clientAccessService.getOwnedProject(
                            authentication, milestone.getProjectId()).isPresent())
                    .toList());
        }
        return ResponseEntity.ok(milestoneService.getAllMilestones());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Milestone>> getMilestonesByProject(
            @PathVariable Integer projectId,
            org.springframework.security.core.Authentication authentication) {

        if (isClient(authentication)
                && clientAccessService.getOwnedProject(authentication, projectId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(milestoneService.getMilestonesByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Milestone> getMilestoneById(
            @PathVariable Integer id,
            org.springframework.security.core.Authentication authentication) {

        if (isClient(authentication)) {
            return milestoneService.getMilestoneById(id)
                    .filter(milestone -> clientAccessService.getOwnedProject(
                            authentication, milestone.getProjectId()).isPresent())
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return milestoneService.getMilestoneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMilestone(
            @RequestBody Milestone milestone) {

        if (milestone.getProjectId() == null) {
            return ResponseEntity.badRequest()
                    .body("Project is required.");
        }

        if (milestone.getMilestoneName() == null
                || milestone.getMilestoneName().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Milestone name is required.");
        }

        if (milestone.getMilestoneDate() == null) {
            return ResponseEntity.badRequest()
                    .body("Milestone date is required.");
        }

        if (milestone.getStatus() == null
                || milestone.getStatus().isBlank()) {

            milestone.setStatus("PENDING");
        }

        return ResponseEntity.ok(
                milestoneService.saveMilestone(milestone)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMilestone(
            @PathVariable Integer id,
            @RequestBody Milestone details) {

        return milestoneService.getMilestoneById(id)
                .map(milestone -> {

                    if (details.getMilestoneName() == null
                            || details.getMilestoneName().isBlank()) {

                        return ResponseEntity.badRequest()
                                .body("Milestone name is required.");
                    }

                    if (details.getMilestoneDate() == null) {

                        return ResponseEntity.badRequest()
                                .body("Milestone date is required.");
                    }

                    milestone.setMilestoneName(
                            details.getMilestoneName()
                    );

                    milestone.setDescription(
                            details.getDescription()
                    );

                    milestone.setMilestoneDate(
                            details.getMilestoneDate()
                    );

                    milestone.setStatus(
                            details.getStatus()
                    );

                    milestone.setProjectId(
                            details.getProjectId()
                    );

                    return ResponseEntity.ok(
                            milestoneService.saveMilestone(milestone)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMilestone(
            @PathVariable Integer id) {

        if (milestoneService.getMilestoneById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        milestoneService.deleteMilestone(id);

        return ResponseEntity.noContent().build();
    }

    private boolean isClient(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT"));
    }
}
