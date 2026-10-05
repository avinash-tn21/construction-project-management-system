package com.example.construction.service;

import com.example.construction.entity.InventoryItem;
import com.example.construction.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }

    public Optional<InventoryItem> getItemById(Integer id) {
        return inventoryRepository.findById(id);
    }

    public InventoryItem saveItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }

    public InventoryItem updateItem(Integer id, InventoryItem item) {
        return inventoryRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setItemName(item.getItemName());
                    existingItem.setCategory(item.getCategory());
                    existingItem.setQuantity(item.getQuantity());
                    existingItem.setUnit(item.getUnit());
                    existingItem.setReorderLevel(item.getReorderLevel());
                    existingItem.setSupplier(item.getSupplier());
                    existingItem.setStatus(item.getStatus());

                    return inventoryRepository.save(existingItem);
                })
                .orElseThrow(() -> new RuntimeException("Inventory item not found with ID: " + id));
    }

    public void deleteItem(Integer id) {
        inventoryRepository.deleteById(id);
    }
}