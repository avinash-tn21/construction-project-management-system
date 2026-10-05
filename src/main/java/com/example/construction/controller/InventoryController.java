package com.example.construction.controller;

import com.example.construction.entity.InventoryItem;
import com.example.construction.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {

        this.inventoryService = inventoryService;
    }


    // =========================
    // GET ALL INVENTORY
    // =========================

    @GetMapping
    public List<InventoryItem> getAllItems() {

        return inventoryService.getAllItems();
    }


    // =========================
    // GET ITEM BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItemById(
            @PathVariable Integer id) {

        return inventoryService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // =========================
    // CREATE ITEM
    // =========================

    @PostMapping
    public ResponseEntity<InventoryItem> createItem(
            @Valid @RequestBody InventoryItem item) {

        InventoryItem savedItem =
                inventoryService.saveItem(item);

        return ResponseEntity.ok(savedItem);
    }


    // =========================
    // UPDATE ITEM
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> updateItem(
            @PathVariable Integer id,
            @Valid @RequestBody InventoryItem item) {

        try {

            InventoryItem updatedItem =
                    inventoryService.updateItem(
                            id,
                            item
                    );

            return ResponseEntity.ok(updatedItem);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }


    // =========================
    // DELETE ITEM
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Integer id) {

        if (inventoryService.getItemById(id).isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        inventoryService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}