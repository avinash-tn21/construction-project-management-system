package com.example.construction.controller;

import com.example.construction.entity.DailySiteLog;
import com.example.construction.entity.SiteProgress;
import com.example.construction.repository.SiteProgressRepository;
import com.example.construction.repository.SiteRepository;
import com.example.construction.repository.UserRepository;
import com.example.construction.service.NotificationService;
import com.example.construction.service.ClientAccessService;
import com.example.construction.service.impl.DailySiteLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-site-logs")
public class DailySiteLogController {

    private final DailySiteLogService dailySiteLogService;
    private final SiteProgressRepository siteProgressRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ClientAccessService clientAccessService;

    public DailySiteLogController(
            DailySiteLogService dailySiteLogService,
            SiteProgressRepository siteProgressRepository,
            SiteRepository siteRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            ClientAccessService clientAccessService) {

        this.dailySiteLogService = dailySiteLogService;
        this.siteProgressRepository = siteProgressRepository;
        this.siteRepository = siteRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.clientAccessService = clientAccessService;
    }

    // =========================================================
    // GET ALL DAILY SITE LOGS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<DailySiteLog>> getAllLogs(
            org.springframework.security.core.Authentication authentication) {
        if (isClient(authentication)) {
            if (clientAccessService.getCurrentClient(authentication).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(dailySiteLogService.getAllLogs().stream()
                    .filter(log -> isOwnedSite(authentication, log.getSiteId()))
                    .toList());
        }

        return ResponseEntity.ok(dailySiteLogService.getAllLogs());
    }

    // =========================================================
    // GET ONE DAILY SITE LOG
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<DailySiteLog> getLogById(
            @PathVariable Integer id,
            org.springframework.security.core.Authentication authentication) {

        if (isClient(authentication)) {
            return dailySiteLogService.getLogById(id)
                    .filter(log -> isOwnedSite(authentication, log.getSiteId()))
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return dailySiteLogService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // CREATE DAILY SITE LOG
    // =========================================================

    @PostMapping
    public ResponseEntity<DailySiteLog> createLog(
            @RequestBody DailySiteLog dailySiteLog) {

        try {

            // -------------------------------------------------
            // Save the daily log first
            // -------------------------------------------------

            DailySiteLog savedLog =
                    dailySiteLogService.createLog(dailySiteLog);

            // -------------------------------------------------
            // Find the site
            // -------------------------------------------------

            var site = siteRepository.findById(
                    savedLog.getSiteId()
            ).orElseThrow(() ->
                    new RuntimeException("Site not found")
            );

            // -------------------------------------------------
            // Get the project connected to the site
            // -------------------------------------------------

            Integer projectId = site.getProjectId();

            // -------------------------------------------------
            // Check whether a SiteProgress already exists
            // for this Daily Site Log.
            //
            // This prevents duplicate progress records if
            // the same request is accidentally submitted twice.
            // -------------------------------------------------

            SiteProgress progress =
                    siteProgressRepository
                            .findByDailyLogId(savedLog.getLogId())
                            .orElse(null);

            // -------------------------------------------------
            // Only create a new SiteProgress if one does not
            // already exist.
            // -------------------------------------------------

            if (progress == null) {

                progress = new SiteProgress();

                progress.setDailyLogId(
                        savedLog.getLogId()
                );
            }

            // -------------------------------------------------
            // Copy Daily Site Log information into SiteProgress
            // -------------------------------------------------

            progress.setProjectId(projectId);

            progress.setProgressDate(
                    savedLog.getLogDate()
            );

            progress.setActivity(
                    savedLog.getActivities()
            );

            progress.setDescription(
                    savedLog.getProgressNotes()
            );

            progress.setProgressPercentage(
                    savedLog.getProgressPercentage()
            );

            progress.setIssues(
                    savedLog.getIssues()
            );

            // -------------------------------------------------
            // Save SiteProgress
            // -------------------------------------------------

            siteProgressRepository.save(progress);

            // =================================================
            // NOTIFICATION:
            // SITE ISSUE → PROJECT MANAGER
            // =================================================

            if (savedLog.getIssues() != null
                    && !savedLog.getIssues().trim().isEmpty()) {

                userRepository.findByRole("PROJECT_MANAGER")
                        .forEach(projectManager -> {

                            notificationService.createNotification(
                                    savedLog.getSupervisorId(),
                                    projectManager.getUserId(),

                                    "Site Issue Reported",

                                    "A site issue was reported for Site ID "
                                            + savedLog.getSiteId()
                                            + ": "
                                            + savedLog.getIssues(),

                                    "SITE_ISSUE",

                                    savedLog.getLogId()
                            );
                        });
            }

            return ResponseEntity.ok(savedLog);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().build();
        }
    }

    // =========================================================
    // UPDATE DAILY SITE LOG
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<DailySiteLog> updateLog(
            @PathVariable Integer id,
            @RequestBody DailySiteLog dailySiteLog) {

        try {

            // -------------------------------------------------
            // Update the daily log
            // -------------------------------------------------

            DailySiteLog updatedLog =
                    dailySiteLogService.updateLog(
                            id,
                            dailySiteLog
                    );

            // -------------------------------------------------
            // Find existing SiteProgress for this Daily Log
            // -------------------------------------------------

            SiteProgress progress =
                    siteProgressRepository
                            .findByDailyLogId(id)
                            .orElse(null);

            // -------------------------------------------------
            // Find the site
            // -------------------------------------------------

            var site = siteRepository.findById(
                    updatedLog.getSiteId()
            ).orElseThrow(() ->
                    new RuntimeException("Site not found")
            );

            // -------------------------------------------------
            // Get project connected to site
            // -------------------------------------------------

            Integer projectId = site.getProjectId();

            // -------------------------------------------------
            // If progress does not exist, create it.
            // Otherwise update the existing record.
            // -------------------------------------------------

            if (progress == null) {

                progress = new SiteProgress();

                progress.setDailyLogId(
                        updatedLog.getLogId()
                );
            }

            // -------------------------------------------------
            // Update SiteProgress
            // -------------------------------------------------

            progress.setProjectId(projectId);

            progress.setProgressDate(
                    updatedLog.getLogDate()
            );

            progress.setActivity(
                    updatedLog.getActivities()
            );

            progress.setDescription(
                    updatedLog.getProgressNotes()
            );

            progress.setProgressPercentage(
                    updatedLog.getProgressPercentage()
            );

            progress.setIssues(
                    updatedLog.getIssues()
            );

            // -------------------------------------------------
            // Save updated SiteProgress
            // -------------------------------------------------

            siteProgressRepository.save(progress);

            // =================================================
            // NOTIFICATION:
            // UPDATED SITE ISSUE → PROJECT MANAGER
            // =================================================

            if (updatedLog.getIssues() != null
                    && !updatedLog.getIssues().trim().isEmpty()) {

                userRepository.findByRole("PROJECT_MANAGER")
                        .forEach(projectManager -> {

                            notificationService.createNotification(
                                    updatedLog.getSupervisorId(),
                                    projectManager.getUserId(),

                                    "Site Issue Updated",

                                    "A site issue was updated for Site ID "
                                            + updatedLog.getSiteId()
                                            + ": "
                                            + updatedLog.getIssues(),

                                    "SITE_ISSUE",

                                    updatedLog.getLogId()
                            );
                        });
            }

            return ResponseEntity.ok(updatedLog);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // DELETE DAILY SITE LOG
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(
            @PathVariable Integer id) {

        try {

            // -------------------------------------------------
            // Delete the linked SiteProgress first
            // -------------------------------------------------

            siteProgressRepository
                    .findByDailyLogId(id)
                    .ifPresent(
                            siteProgressRepository::delete
                    );

            // -------------------------------------------------
            // Delete the Daily Site Log
            // -------------------------------------------------

            dailySiteLogService.deleteLog(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    private boolean isOwnedSite(
            org.springframework.security.core.Authentication authentication,
            Integer siteId) {
        return siteRepository.findById(siteId)
                .flatMap(site -> clientAccessService.getOwnedProject(
                        authentication, site.getProjectId()))
                .isPresent();
    }

    private boolean isClient(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT"));
    }
}
