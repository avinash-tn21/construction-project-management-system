package com.example.construction.controller;

import com.example.construction.entity.EquipmentAllocation;
import com.example.construction.entity.User;
import com.example.construction.repository.UserRepository;
import com.example.construction.service.NotificationService;
import com.example.construction.service.impl.EquipmentAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-allocations")
public class EquipmentAllocationController {

    private final EquipmentAllocationService allocationService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public EquipmentAllocationController(
            EquipmentAllocationService allocationService,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.allocationService = allocationService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /*
     * =========================================================
     * GET ALL ALLOCATIONS
     * =========================================================
     */

    @GetMapping
    public List<EquipmentAllocation> getAllAllocations() {

        return allocationService.getAllAllocations();
    }

    /*
     * =========================================================
     * GET ALLOCATION BY ID
     * =========================================================
     */

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentAllocation> getAllocationById(
            @PathVariable Integer id) {

        return allocationService.getAllocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * =========================================================
     * CREATE ALLOCATION
     * =========================================================
     *
     * Project Manager creates a request.
     *
     * Equipment Manager receives:
     *      EQUIPMENT_REQUEST
     */

    @PostMapping
    public ResponseEntity<?> createAllocation(
            @RequestBody EquipmentAllocation allocation,
            Authentication authentication) {

        try {

            /*
             * New allocations should normally be PENDING.
             */
            if (allocation.getStatus() == null ||
                    allocation.getStatus().trim().isEmpty()) {

                allocation.setStatus("PENDING");
            }

            EquipmentAllocation savedAllocation =
                    allocationService.saveAllocation(
                            allocation
                    );

            /*
             * Find the user who created the request.
             */
            User sender =
                    userRepository.findByUsername(
                            authentication.getName()
                    ).orElse(null);

            /*
             * Notify all Equipment Managers.
             */
            userRepository.findByRole("EQUIPMENT_MANAGER")
                    .forEach(equipmentManager -> {

                        String equipmentName =
                                savedAllocation.getEquipment() != null
                                        ? savedAllocation
                                        .getEquipment()
                                        .getEquipmentName()
                                        : "Unknown equipment";

                        String projectName =
                                savedAllocation.getProject() != null
                                        ? savedAllocation
                                        .getProject()
                                        .getProjectName()
                                        : "Unknown project";

                        String siteName =
                                savedAllocation.getSite() != null
                                        ? savedAllocation
                                        .getSite()
                                        .getSiteName()
                                        : "Unknown site";

                        notificationService.createNotification(

                                sender != null
                                        ? sender.getUserId()
                                        : null,

                                equipmentManager.getUserId(),

                                "Equipment Allocation Request",

                                "A new equipment allocation request "
                                        + "has been created for "
                                        + equipmentName
                                        + " at "
                                        + siteName
                                        + " for project "
                                        + projectName
                                        + ".",

                                "EQUIPMENT_REQUEST",

                                savedAllocation.getAllocationId()
                        );
                    });

            return ResponseEntity.ok(savedAllocation);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /*
     * =========================================================
     * UPDATE ALLOCATION
     * =========================================================
     *
     * APPROVED:
     *      Notify Project Manager.
     *
     * REJECTED:
     *      Notify Project Manager.
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAllocation(
            @PathVariable Integer id,
            @RequestBody EquipmentAllocation allocation,
            Authentication authentication) {

        try {

            EquipmentAllocation updated =
                    allocationService.updateAllocation(
                            id,
                            allocation
                    );

            /*
             * Find the user who performed the update.
             */
            User sender =
                    userRepository.findByUsername(
                            authentication.getName()
                    ).orElse(null);

            /*
             * Get equipment/project names safely.
             */
            String equipmentName =
                    updated.getEquipment() != null
                            ? updated.getEquipment()
                            .getEquipmentName()
                            : "Unknown equipment";

            String projectName =
                    updated.getProject() != null
                            ? updated.getProject()
                            .getProjectName()
                            : "Unknown project";

            /*
             * =================================================
             * APPROVED
             * =================================================
             */

            if ("APPROVED".equalsIgnoreCase(
                    updated.getStatus())) {

                userRepository.findByRole("PROJECT_MANAGER")
                        .forEach(projectManager -> {

                            notificationService
                                    .createNotification(

                                            sender != null
                                                    ? sender.getUserId()
                                                    : null,

                                            projectManager.getUserId(),

                                            "Equipment Request Approved",

                                            "The equipment request for "
                                                    + equipmentName
                                                    + " for project "
                                                    + projectName
                                                    + " has been approved.",

                                            "EQUIPMENT_RESPONSE",

                                            updated.getAllocationId()
                                    );
                        });
            }

            /*
             * =================================================
             * REJECTED
             * =================================================
             */

            else if ("REJECTED".equalsIgnoreCase(
                    updated.getStatus())) {

                userRepository.findByRole("PROJECT_MANAGER")
                        .forEach(projectManager -> {

                            notificationService
                                    .createNotification(

                                            sender != null
                                                    ? sender.getUserId()
                                                    : null,

                                            projectManager.getUserId(),

                                            "Equipment Request Rejected",

                                            "The equipment request for "
                                                    + equipmentName
                                                    + " for project "
                                                    + projectName
                                                    + " has been rejected.",

                                            "EQUIPMENT_RESPONSE",

                                            updated.getAllocationId()
                                    );
                        });
            }

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /*
     * =========================================================
     * DELETE ALLOCATION
     * =========================================================
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAllocation(
            @PathVariable Integer id) {

        try {

            if (allocationService
                    .getAllocationById(id)
                    .isEmpty()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            allocationService.deleteAllocation(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}