package com.example.construction.repository;

import com.example.construction.entity.SiteProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteProgressRepository extends JpaRepository<SiteProgress, Integer> {
}