package com.example.construction.service.impl;

import com.example.construction.entity.DailySiteLog;
import com.example.construction.repository.DailySiteLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DailySiteLogService {

    private final DailySiteLogRepository dailySiteLogRepository;

    public DailySiteLogService(DailySiteLogRepository dailySiteLogRepository) {
        this.dailySiteLogRepository = dailySiteLogRepository;
    }

    public List<DailySiteLog> getAllLogs() {
        return dailySiteLogRepository.findAll();
    }

    public Optional<DailySiteLog> getLogById(Integer id) {
        return dailySiteLogRepository.findById(id);
    }

    public DailySiteLog createLog(DailySiteLog dailySiteLog) {
        return dailySiteLogRepository.save(dailySiteLog);
    }

    public DailySiteLog updateLog(Integer id, DailySiteLog updatedLog) {
        return dailySiteLogRepository.findById(id)
                .map(existingLog -> {
                    existingLog.setSiteId(updatedLog.getSiteId());
                    existingLog.setSupervisorId(updatedLog.getSupervisorId());
                    existingLog.setLogDate(updatedLog.getLogDate());
                    existingLog.setWeatherCondition(updatedLog.getWeatherCondition());
                    existingLog.setTemperature(updatedLog.getTemperature());
                    existingLog.setActivities(updatedLog.getActivities());
                    existingLog.setProgressNotes(updatedLog.getProgressNotes());
                    existingLog.setIssues(updatedLog.getIssues());

                    return dailySiteLogRepository.save(existingLog);
                })
                .orElseThrow(() -> new RuntimeException("Daily site log not found"));
    }

    public void deleteLog(Integer id) {
        dailySiteLogRepository.deleteById(id);
    }
}