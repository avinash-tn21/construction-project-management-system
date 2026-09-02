package com.example.construction.controller;

import com.example.construction.entity.DailySiteLog;
import com.example.construction.service.impl.DailySiteLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-site-logs")
public class DailySiteLogController {

    private final DailySiteLogService dailySiteLogService;

    public DailySiteLogController(DailySiteLogService dailySiteLogService) {
        this.dailySiteLogService = dailySiteLogService;
    }

    @GetMapping
    public List<DailySiteLog> getAllLogs() {
        return dailySiteLogService.getAllLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailySiteLog> getLogById(@PathVariable Integer id) {
        return dailySiteLogService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DailySiteLog createLog(@RequestBody DailySiteLog dailySiteLog) {
        return dailySiteLogService.createLog(dailySiteLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailySiteLog> updateLog(
            @PathVariable Integer id,
            @RequestBody DailySiteLog dailySiteLog) {

        try {
            return ResponseEntity.ok(
                    dailySiteLogService.updateLog(id, dailySiteLog)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Integer id) {
        dailySiteLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}