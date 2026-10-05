package com.example.construction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;

    @NotBlank(message = "Item name is required.")
    @Size(min = 2, max = 150,
            message = "Item name must be between 2 and 150 characters.")
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @NotBlank(message = "Category is required.")
    @Size(max = 100,
            message = "Category cannot exceed 100 characters.")
    private String category;

    @NotNull(message = "Quantity is required.")
    @Min(value = 0,
            message = "Quantity cannot be negative.")
    @Column(nullable = false)
    private Integer quantity;

    @NotBlank(message = "Unit is required.")
    @Size(max = 30,
            message = "Unit cannot exceed 30 characters.")
    private String unit;

    @NotNull(message = "Reorder level is required.")
    @Min(value = 0,
            message = "Reorder level cannot be negative.")
    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Size(max = 150,
            message = "Supplier name cannot exceed 150 characters.")
    private String supplier;

    @NotBlank(message = "Status is required.")
    private String status;

    public InventoryItem() {
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}