package com.example.construction.controller;

import com.example.construction.entity.Equipment;
import com.example.construction.entity.EquipmentAllocation;
import com.example.construction.entity.Project;
import com.example.construction.entity.Site;
import com.example.construction.entity.User;
import com.example.construction.repository.EquipmentAllocationRepository;
import com.example.construction.repository.EquipmentRepository;
import com.example.construction.repository.ProjectRepository;
import com.example.construction.repository.SiteRepository;
import com.example.construction.repository.UserRepository;
import com.example.construction.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipment-allocations")
public class EquipmentAllocationController {

    private final EquipmentAllocationRepository allocationRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProjectRepository projectRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public EquipmentAllocationController(
            EquipmentAllocationRepository allocationRepository,
            EquipmentRepository equipmentRepository,
            ProjectRepository projectRepository,
            SiteRepository siteRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.allocationRepository = allocationRepository;
        this.equipmentRepository = equipmentRepository;
        this.projectRepository = projectRepository;
        this.siteRepository = siteRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // =========================================================
    // GET ALL ALLOCATIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<EquipmentAllocation>> getAllAllocations() {

        return ResponseEntity.ok(
                allocationRepository.findAll()
        );
    }

    // =========================================================
    // GET ALLOCATION BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentAllocation> getAllocationById(
            @PathVariable Integer id) {

        return allocationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // =========================================================
    // CREATE EQUIPMENT ALLOCATION
    // =========================================================

    @PostMapping
    public ResponseEntity<EquipmentAllocation> createAllocation(
            @RequestBody EquipmentAllocation allocation) {

        // -----------------------------------------------------
        // Validate Equipment
        // -----------------------------------------------------

        if (allocation.getEquipment() == null
                || allocation.getEquipment().getEquipmentId() == null) {

            return ResponseEntity.badRequest().build();
        }

        Optional<Equipment> equipmentOptional =
                equipmentRepository.findById(
                        allocation.getEquipment().getEquipmentId()
                );

        if (equipmentOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Equipment equipment = equipmentOptional.get();

        // -----------------------------------------------------
        // Validate Project
        // -----------------------------------------------------

        if (allocation.getProject() == null
                || allocation.getProject().getProjectId() == null) {

            return ResponseEntity.badRequest().build();
        }

        Optional<Project> projectOptional =
                projectRepository.findById(
                        allocation.getProject().getProjectId()
                );

        if (projectOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Project project = projectOptional.get();

        // -----------------------------------------------------
        // Validate Site
        // -----------------------------------------------------

        if (allocation.getSite() == null
                || allocation.getSite().getSiteId() == null) {

            return ResponseEntity.badRequest().build();
        }

        Optional<Site> siteOptional =
                siteRepository.findById(
                        allocation.getSite().getSiteId()
                );

        if (siteOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Site site = siteOptional.get();

        // -----------------------------------------------------
        // Set managed entities
        // -----------------------------------------------------

        allocation.setEquipment(equipment);
        allocation.setProject(project);
        allocation.setSite(site);

        // -----------------------------------------------------
        // Save allocation
        // -----------------------------------------------------

        EquipmentAllocation savedAllocation =
                allocationRepository.save(allocation);

        // -----------------------------------------------------
        // Get useful names for notification
        // -----------------------------------------------------

        String equipmentName =
                equipment.getEquipmentName();

        String projectName =
                project.getProjectName();

        String siteName =
                site.getSiteName();

        // -----------------------------------------------------
        // Notify Equipment Managers
        // -----------------------------------------------------

        List<User> equipmentManagers =
                userRepository.findByRole("EQUIPMENT_MANAGER");

        String notificationMessage =
                "A new equipment request has been submitted.\n\n"
                        + "Equipment: " + equipmentName + "\n"
                        + "Project: " + projectName + "\n"
                        + "Site: " + siteName + "\n"
                        + "Allocated Date: "
                        + savedAllocation.getAllocatedDate() + "\n"
                        + "Status: "
                        + savedAllocation.getStatus();

        for (User manager : equipmentManagers) {

            notificationService.createNotification(
                    null,
                    manager.getUserId(),
                    "New Equipment Request",
                    notificationMessage,
                    "EQUIPMENT_REQUEST",
                    savedAllocation.getAllocationId()
            );
        }

        return ResponseEntity.ok(savedAllocation);
    }

    // =========================================================
    // UPDATE EQUIPMENT ALLOCATION
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentAllocation> updateAllocation(
            @PathVariable Integer id,
            @RequestBody EquipmentAllocation updatedAllocation) {

        return allocationRepository.findById(id)
                .map(existing -> {

                    // -------------------------------------------------
                    // Update Equipment
                    // -------------------------------------------------

                    if (updatedAllocation.getEquipment() != null
                            && updatedAllocation.getEquipment()
                            .getEquipmentId() != null) {

                        equipmentRepository.findById(
                                updatedAllocation.getEquipment()
                                        .getEquipmentId()
                        ).ifPresent(existing::setEquipment);
                    }

                    // -------------------------------------------------
                    // Update Project
                    // -------------------------------------------------

                    if (updatedAllocation.getProject() != null
                            && updatedAllocation.getProject()
                            .getProjectId() != null) {

                        projectRepository.findById(
                                updatedAllocation.getProject()
                                        .getProjectId()
                        ).ifPresent(existing::setProject);
                    }

                    // -------------------------------------------------
                    // Update Site
                    // -------------------------------------------------

                    if (updatedAllocation.getSite() != null
                            && updatedAllocation.getSite()
                            .getSiteId() != null) {

                        siteRepository.findById(
                                updatedAllocation.getSite()
                                        .getSiteId()
                        ).ifPresent(existing::setSite);
                    }

                    // -------------------------------------------------
                    // Update other fields
                    // -------------------------------------------------

                    existing.setAllocatedDate(
                            updatedAllocation.getAllocatedDate()
                    );

                    existing.setReturnDate(
                            updatedAllocation.getReturnDate()
                    );

                    existing.setStatus(
                            updatedAllocation.getStatus()
                    );

                    existing.setRemarks(
                            updatedAllocation.getRemarks()
                    );

                    EquipmentAllocation saved =
                            allocationRepository.save(existing);

                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // =========================================================
    // DELETE EQUIPMENT ALLOCATION
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocation(
            @PathVariable Integer id) {

        if (!allocationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        allocationRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // APPROVE EQUIPMENT REQUEST
    // =========================================================

    @PutMapping("/{id}/approve")
    public ResponseEntity<EquipmentAllocation> approveAllocation(
            @PathVariable Integer id) {

        return allocationRepository.findById(id)
                .map(allocation -> {

                    allocation.setStatus("APPROVED");

                    EquipmentAllocation saved =
                            allocationRepository.save(allocation);

                    // ---------------------------------------------
                    // Notify Project Managers
                    // ---------------------------------------------

                    List<User> projectManagers =
                            userRepository.findByRole(
                                    "PROJECT_MANAGER"
                            );

                    String equipmentName =
                            allocation.getEquipment()
                                    .getEquipmentName();

                    String projectName =
                            allocation.getProject()
                                    .getProjectName();

                    String siteName =
                            allocation.getSite()
                                    .getSiteName();

                    String message =
                            "The equipment request has been approved.\n\n"
                                    + "Equipment: "
                                    + equipmentName + "\n"
                                    + "Project: "
                                    + projectName + "\n"
                                    + "Site: "
                                    + siteName;

                    for (User manager : projectManagers) {

                        notificationService.createNotification(
                                null,
                                manager.getUserId(),
                                "Equipment Request Approved",
                                message,
                                "EQUIPMENT_APPROVED",
                                allocation.getAllocationId()
                        );
                    }

                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // =========================================================
    // REJECT EQUIPMENT REQUEST
    // =========================================================

    @PutMapping("/{id}/reject")
    public ResponseEntity<EquipmentAllocation> rejectAllocation(
            @PathVariable Integer id) {

        return allocationRepository.findById(id)
                .map(allocation -> {

                    allocation.setStatus("REJECTED");

                    EquipmentAllocation saved =
                            allocationRepository.save(allocation);

                    // ---------------------------------------------
                    // Notify Project Managers
                    // ---------------------------------------------

                    List<User> projectManagers =
                            userRepository.findByRole(
                                    "PROJECT_MANAGER"
                            );

                    String equipmentName =
                            allocation.getEquipment()
                                    .getEquipmentName();

                    String projectName =
                            allocation.getProject()
                                    .getProjectName();

                    String siteName =
                            allocation.getSite()
                                    .getSiteName();

                    String message =
                            "The equipment request has been rejected.\n\n"
                                    + "Equipment: "
                                    + equipmentName + "\n"
                                    + "Project: "
                                    + projectName + "\n"
                                    + "Site: "
                                    + siteName;

                    for (User manager : projectManagers) {

                        notificationService.createNotification(
                                null,
                                manager.getUserId(),
                                "Equipment Request Rejected",
                                message,
                                "EQUIPMENT_REJECTED",
                                allocation.getAllocationId()
                        );
                    }

                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }
}