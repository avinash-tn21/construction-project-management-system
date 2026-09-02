package com.example.construction.repository;

import com.example.construction.entity.MaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Integer> {
}