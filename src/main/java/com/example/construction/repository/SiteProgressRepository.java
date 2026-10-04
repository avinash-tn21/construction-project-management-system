package com.example.construction.repository;

import com.example.construction.entity.SiteProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteProgressRepository
        extends JpaRepository<SiteProgress, Integer> {

    List<SiteProgress> findByProjectIdOrderByProgressDateDesc(
            Integer projectId
    );

    Optional<SiteProgress> findByDailyLogId(Integer dailyLogId);
}