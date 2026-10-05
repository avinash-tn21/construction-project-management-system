package com.example.construction.service.impl;

import com.example.construction.entity.DailySiteLog;
import com.example.construction.entity.Site;
import com.example.construction.entity.SiteProgress;
import com.example.construction.repository.DailySiteLogRepository;
import com.example.construction.repository.SiteProgressRepository;
import com.example.construction.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DailySiteLogService {

    private final DailySiteLogRepository dailySiteLogRepository;
    private final SiteRepository siteRepository;
    private final SiteProgressRepository siteProgressRepository;

    public DailySiteLogService(
            DailySiteLogRepository dailySiteLogRepository,
            SiteRepository siteRepository,
            SiteProgressRepository siteProgressRepository) {

        this.dailySiteLogRepository = dailySiteLogRepository;
        this.siteRepository = siteRepository;
        this.siteProgressRepository = siteProgressRepository;
    }

    public List<DailySiteLog> getAllLogs() {
        return dailySiteLogRepository.findAll();
    }

    public Optional<DailySiteLog> getLogById(Integer id) {
        return dailySiteLogRepository.findById(id);
    }

    public DailySiteLog createLog(DailySiteLog dailySiteLog) {

        // First save the daily log
        DailySiteLog savedLog =
                dailySiteLogRepository.save(dailySiteLog);

        // Find the site
        Site site = siteRepository.findById(savedLog.getSiteId())
                .orElseThrow(() ->
                        new RuntimeException("Site not found"));

        // Create progress record for the client dashboard
        SiteProgress progress = new SiteProgress();

        progress.setProjectId(site.getProjectId());
        progress.setProgressDate(savedLog.getLogDate());
        progress.setActivity(savedLog.getActivities());
        progress.setDescription(savedLog.getProgressNotes());
        progress.setProgressPercentage(
                savedLog.getProgressPercentage()
        );
        progress.setIssues(savedLog.getIssues());
        progress.setDailyLogId(savedLog.getLogId());

        siteProgressRepository.save(progress);

        return savedLog;
    }

    public DailySiteLog updateLog(
            Integer id,
            DailySiteLog updatedLog) {

        return dailySiteLogRepository.findById(id)
                .map(existingLog -> {

                    existingLog.setSiteId(updatedLog.getSiteId());
                    existingLog.setSupervisorId(
                            updatedLog.getSupervisorId()
                    );
                    existingLog.setLogDate(
                            updatedLog.getLogDate()
                    );
                    existingLog.setWeatherCondition(
                            updatedLog.getWeatherCondition()
                    );
                    existingLog.setTemperature(
                            updatedLog.getTemperature()
                    );
                    existingLog.setActivities(
                            updatedLog.getActivities()
                    );
                    existingLog.setProgressNotes(
                            updatedLog.getProgressNotes()
                    );
                    existingLog.setProgressPercentage(
                            updatedLog.getProgressPercentage()
                    );
                    existingLog.setIssues(
                            updatedLog.getIssues()
                    );

                    DailySiteLog savedLog =
                            dailySiteLogRepository.save(existingLog);

                    // Update corresponding SiteProgress record
                    Site site = siteRepository
                            .findById(savedLog.getSiteId())
                            .orElseThrow(() ->
                                    new RuntimeException("Site not found"));

                    SiteProgress progress =
                            siteProgressRepository
                                    .findByDailyLogId(savedLog.getLogId())
                                    .orElseGet(SiteProgress::new);

                    progress.setProjectId(site.getProjectId());
                    progress.setProgressDate(savedLog.getLogDate());
                    progress.setActivity(savedLog.getActivities());
                    progress.setDescription(
                            savedLog.getProgressNotes()
                    );
                    progress.setProgressPercentage(
                            savedLog.getProgressPercentage()
                    );
                    progress.setIssues(savedLog.getIssues());
                    progress.setDailyLogId(savedLog.getLogId());

                    siteProgressRepository.save(progress);

                    return savedLog;
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Daily site log not found"
                        ));
    }

    public void deleteLog(Integer id) {

        // Delete corresponding progress record first
        siteProgressRepository
                .findByDailyLogId(id)
                .ifPresent(siteProgressRepository::delete);

        dailySiteLogRepository.deleteById(id);
    }
}