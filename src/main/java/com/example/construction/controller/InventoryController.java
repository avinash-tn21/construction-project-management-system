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

    @GetMapping
    public List<InventoryItem> getAllItems() {
        return inventoryService.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItemById(
            @PathVariable Integer id) {

        return inventoryService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createItem(
            @Valid @RequestBody InventoryItem item) {

        return ResponseEntity.ok(
                inventoryService.saveItem(item)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> updateItem(
            @PathVariable Integer id,
            @Valid @RequestBody InventoryItem item) {

        try {
            return ResponseEntity.ok(
                    inventoryService.updateItem(id, item)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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