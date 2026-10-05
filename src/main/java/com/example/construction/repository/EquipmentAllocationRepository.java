package com.example.construction.repository;

import com.example.construction.entity.EquipmentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentAllocationRepository
        extends JpaRepository<EquipmentAllocation, Integer> {
}