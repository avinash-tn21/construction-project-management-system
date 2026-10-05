package com.example.construction.service.impl;

import com.example.construction.entity.Equipment;
import com.example.construction.entity.EquipmentAllocation;
import com.example.construction.repository.EquipmentAllocationRepository;
import com.example.construction.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentAllocationService {

    private final EquipmentAllocationRepository allocationRepository;
    private final EquipmentRepository equipmentRepository;

    public EquipmentAllocationService(
            EquipmentAllocationRepository allocationRepository,
            EquipmentRepository equipmentRepository) {

        this.allocationRepository = allocationRepository;
        this.equipmentRepository = equipmentRepository;
    }

    /*
     * =========================================================
     * GET ALL ALLOCATIONS
     * =========================================================
     */

    public List<EquipmentAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    /*
     * =========================================================
     * GET ALLOCATION BY ID
     * =========================================================
     */

    public Optional<EquipmentAllocation> getAllocationById(Integer id) {
        return allocationRepository.findById(id);
    }

    /*
     * =========================================================
     * CREATE ALLOCATION
     * =========================================================
     *
     * New allocations normally start as PENDING.
     *
     * PENDING:
     *      Equipment remains AVAILABLE.
     *
     * APPROVED:
     *      Equipment becomes IN USE.
     *
     * REJECTED:
     *      Equipment remains AVAILABLE.
     *
     * RETURNED:
     *      Equipment becomes AVAILABLE.
     */

    @Transactional
    public EquipmentAllocation saveAllocation(
            EquipmentAllocation allocation) {

        if (allocation.getEquipment() == null ||
                allocation.getEquipment().getEquipmentId() == null) {

            throw new IllegalArgumentException(
                    "Equipment must be selected."
            );
        }

        Equipment equipment =
                equipmentRepository.findById(
                        allocation.getEquipment().getEquipmentId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Equipment not found."
                        )
                );

        String status =
                allocation.getStatus() == null
                        ? "PENDING"
                        : allocation.getStatus().toUpperCase();

        /*
         * Only AVAILABLE equipment can be requested.
         */
        if ("PENDING".equals(status)
                || "APPROVED".equals(status)) {

            if (!"AVAILABLE".equalsIgnoreCase(
                    equipment.getStatus())) {

                throw new IllegalStateException(
                        "This equipment is not available for allocation."
                );
            }
        }

        /*
         * APPROVED means the equipment is now in use.
         */
        if ("APPROVED".equals(status)) {

            equipment.setStatus("IN USE");

            equipmentRepository.save(equipment);
        }

        /*
         * Use the managed equipment entity.
         */
        allocation.setEquipment(equipment);

        /*
         * Make sure null status does not get saved.
         */
        allocation.setStatus(status);

        return allocationRepository.save(allocation);
    }

    /*
     * =========================================================
     * UPDATE ALLOCATION
     * =========================================================
     */

    @Transactional
    public EquipmentAllocation updateAllocation(
            Integer id,
            EquipmentAllocation allocation) {

        EquipmentAllocation existing =
                allocationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Allocation not found with ID: " + id
                                )
                        );

        if (allocation.getEquipment() == null ||
                allocation.getEquipment().getEquipmentId() == null) {

            throw new IllegalArgumentException(
                    "Equipment must be selected."
            );
        }

        /*
         * Existing equipment.
         */
        Equipment oldEquipment =
                existing.getEquipment();

        /*
         * Newly selected equipment.
         */
        Equipment newEquipment =
                equipmentRepository.findById(
                        allocation.getEquipment().getEquipmentId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Equipment not found."
                        )
                );

        String newStatus =
                allocation.getStatus() == null
                        ? "PENDING"
                        : allocation.getStatus().toUpperCase();

        /*
         * =====================================================
         * IF EQUIPMENT WAS CHANGED
         * =====================================================
         */

        if (oldEquipment != null &&
                !oldEquipment.getEquipmentId()
                        .equals(newEquipment.getEquipmentId())) {

            /*
             * Release old equipment if it was being used.
             */
            if ("IN USE".equalsIgnoreCase(
                    oldEquipment.getStatus())) {

                oldEquipment.setStatus("AVAILABLE");

                equipmentRepository.save(oldEquipment);
            }
        }

        /*
         * =====================================================
         * PENDING
         * =====================================================
         *
         * Equipment must remain available.
         */

        if ("PENDING".equals(newStatus)) {

            /*
             * If changing to another equipment item,
             * the new equipment must be available.
             */
            if (oldEquipment == null ||
                    !oldEquipment.getEquipmentId()
                            .equals(newEquipment.getEquipmentId())) {

                if (!"AVAILABLE".equalsIgnoreCase(
                        newEquipment.getStatus())) {

                    throw new IllegalStateException(
                            "The selected equipment is not available."
                    );
                }
            }
        }

        /*
         * =====================================================
         * APPROVED
         * =====================================================
         *
         * Equipment becomes IN USE.
         */

        else if ("APPROVED".equals(newStatus)) {

            /*
             * If this is a different equipment item,
             * it must be available.
             */
            if (oldEquipment == null ||
                    !oldEquipment.getEquipmentId()
                            .equals(newEquipment.getEquipmentId())) {

                if (!"AVAILABLE".equalsIgnoreCase(
                        newEquipment.getStatus())) {

                    throw new IllegalStateException(
                            "The selected equipment is not available."
                    );
                }
            }

            /*
             * If it is already IN USE because the allocation
             * was previously approved, keep it IN USE.
             */
            newEquipment.setStatus("IN USE");
        }

        /*
         * =====================================================
         * REJECTED
         * =====================================================
         *
         * Equipment remains AVAILABLE.
         */

        else if ("REJECTED".equals(newStatus)) {

            newEquipment.setStatus("AVAILABLE");
        }

        /*
         * =====================================================
         * RETURNED
         * =====================================================
         *
         * Equipment is returned and becomes AVAILABLE.
         */

        else if ("RETURNED".equals(newStatus)) {

            newEquipment.setStatus("AVAILABLE");
        }

        /*
         * Reject unknown statuses.
         */

        else {

            throw new IllegalArgumentException(
                    "Invalid allocation status: " + newStatus
            );
        }

        /*
         * Save equipment status.
         */
        equipmentRepository.save(newEquipment);

        /*
         * Update allocation.
         */
        existing.setEquipment(newEquipment);

        existing.setProject(
                allocation.getProject()
        );

        existing.setSite(
                allocation.getSite()
        );

        existing.setAllocatedDate(
                allocation.getAllocatedDate()
        );

        existing.setReturnDate(
                allocation.getReturnDate()
        );

        existing.setStatus(
                newStatus
        );

        existing.setRemarks(
                allocation.getRemarks()
        );

        return allocationRepository.save(existing);
    }

    /*
     * =========================================================
     * DELETE ALLOCATION
     * =========================================================
     */

    @Transactional
    public void deleteAllocation(Integer id) {

        EquipmentAllocation allocation =
                allocationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Allocation not found with ID: " + id
                                )
                        );

        /*
         * If the allocation is currently using equipment,
         * make that equipment available again.
         *
         * This includes APPROVED and the older ALLOCATED
         * status in case old database records still exist.
         */

        if (allocation.getEquipment() != null &&
                (
                        "APPROVED".equalsIgnoreCase(
                                allocation.getStatus()
                        )
                                ||
                                "ALLOCATED".equalsIgnoreCase(
                                        allocation.getStatus()
                                )
                )) {

            Equipment equipment =
                    allocation.getEquipment();

            equipment.setStatus("AVAILABLE");

            equipmentRepository.save(equipment);
        }

        allocationRepository.delete(allocation);
    }
}