package com.example.construction.controller;

import com.example.construction.entity.Equipment;
import com.example.construction.service.impl.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {

        this.equipmentService = equipmentService;
    }


    // =========================
    // GET ALL EQUIPMENT
    // =========================

    @GetMapping
    public List<Equipment> getAllEquipment() {

        return equipmentService.getAllEquipment();
    }


    // =========================
    // GET EQUIPMENT BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(
            @PathVariable Integer id) {

        return equipmentService.getEquipmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // =========================
    // CREATE EQUIPMENT
    // =========================

    @PostMapping
    public ResponseEntity<Equipment> createEquipment(
            @RequestBody Equipment equipment) {

        Equipment savedEquipment =
                equipmentService.saveEquipment(equipment);

        return ResponseEntity.ok(savedEquipment);
    }


    // =========================
    // UPDATE EQUIPMENT
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable Integer id,
            @RequestBody Equipment equipment) {

        try {

            Equipment updatedEquipment =
                    equipmentService.updateEquipment(
                            id,
                            equipment
                    );

            return ResponseEntity.ok(updatedEquipment);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }


    // =========================
    // DELETE EQUIPMENT
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Integer id) {

        if (equipmentService.getEquipmentById(id).isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        equipmentService.deleteEquipment(id);

        return ResponseEntity.noContent().build();
    }
}