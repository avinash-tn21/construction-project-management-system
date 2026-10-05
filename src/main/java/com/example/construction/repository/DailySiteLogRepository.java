package com.example.construction.repository;

import com.example.construction.entity.DailySiteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailySiteLogRepository extends JpaRepository<DailySiteLog, Integer> {
}