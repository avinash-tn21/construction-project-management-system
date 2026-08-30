package com.example.construction.repository;

import com.example.construction.entity.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractorRepository extends JpaRepository<Contractor, Integer> {
}