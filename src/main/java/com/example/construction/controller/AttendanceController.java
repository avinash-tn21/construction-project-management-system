package com.example.construction.controller;

import com.example.construction.entity.Attendance;
import com.example.construction.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(
            @PathVariable Integer id) {

        return attendanceService.getAttendanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Attendance createAttendance(
            @RequestBody Attendance attendance) {

        return attendanceService.saveAttendance(attendance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable Integer id,
            @RequestBody Attendance attendance) {

        return attendanceService.getAttendanceById(id)
                .map(existingAttendance -> {

                    attendance.setAttendanceId(id);

                    return ResponseEntity.ok(
                            attendanceService.saveAttendance(attendance)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/clock-in/{workerId}")
    public ResponseEntity<?> clockIn(
            @PathVariable Integer workerId) {

        try {

            Attendance attendance =
                    attendanceService.clockIn(workerId);

            return ResponseEntity.ok(attendance);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/clock-out/{workerId}")
    public ResponseEntity<?> clockOut(
            @PathVariable Integer workerId) {

        try {

            Attendance attendance =
                    attendanceService.clockOut(workerId);

            return ResponseEntity.ok(attendance);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Integer id) {

        if (attendanceService.getAttendanceById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        attendanceService.deleteAttendance(id);

        return ResponseEntity.noContent().build();
    }
}