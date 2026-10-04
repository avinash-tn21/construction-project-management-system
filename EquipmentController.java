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

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentService.getAllEquipment();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(
            @PathVariable Integer id) {

        return equipmentService.getEquipmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Equipment> createEquipment(
            @RequestBody Equipment equipment) {

        return ResponseEntity.ok(
                equipmentService.saveEquipment(equipment)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable Integer id,
            @RequestBody Equipment equipment) {

        try {

            return ResponseEntity.ok(
                    equipmentService.updateEquipment(
                            id,
                            equipment
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Integer id) {

        if (equipmentService
                .getEquipmentById(id)
                .isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        equipmentService.deleteEquipment(id);

        return ResponseEntity.noContent().build();
    }
}