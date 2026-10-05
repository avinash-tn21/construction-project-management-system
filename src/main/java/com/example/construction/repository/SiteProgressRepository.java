package com.example.construction.repository;

import com.example.construction.entity.SiteProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteProgressRepository extends JpaRepository<SiteProgress, Integer> {

    // Used by the main management dashboard
    List<SiteProgress> findByProjectIdOrderByProgressDateDesc(
            Integer projectId
    );

    // Used by the client dashboard
    List<SiteProgress> findByProjectIdOrderByProgressDateDescProgressIdDesc(
            Integer projectId
    );

    // Used by DailySiteLogController
    Optional<SiteProgress> findByDailyLogId(
            Integer dailyLogId
    );
}