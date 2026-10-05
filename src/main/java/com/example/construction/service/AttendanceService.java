package com.example.construction.service;

import com.example.construction.entity.Attendance;
import com.example.construction.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getAttendanceById(Integer id) {
        return attendanceRepository.findById(id);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Integer id) {
        attendanceRepository.deleteById(id);
    }

    public Attendance clockIn(Integer workerId) {

        LocalDate today = LocalDate.now();

        Optional<Attendance> existing =
                attendanceRepository.findByWorkerIdAndAttendanceDate(
                        workerId,
                        today
                );

        if (existing.isPresent()) {

            Attendance attendance = existing.get();

            if (attendance.getCheckIn() != null) {
                throw new RuntimeException(
                        "Worker has already clocked in today."
                );
            }

            attendance.setCheckIn(LocalTime.now());
            attendance.setStatus("PRESENT");

            return attendanceRepository.save(attendance);
        }

        Attendance attendance = new Attendance();

        attendance.setWorkerId(workerId);
        attendance.setAttendanceDate(today);
        attendance.setStatus("PRESENT");
        attendance.setCheckIn(LocalTime.now());

        return attendanceRepository.save(attendance);
    }

    public Attendance clockOut(Integer workerId) {

        LocalDate today = LocalDate.now();

        Attendance attendance =
                attendanceRepository
                        .findByWorkerIdAndAttendanceDate(
                                workerId,
                                today
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No clock-in record found for this worker today."
                                )
                        );

        if (attendance.getCheckIn() == null) {
            throw new RuntimeException(
                    "Worker has not clocked in yet."
            );
        }

        if (attendance.getCheckOut() != null) {
            throw new RuntimeException(
                    "Worker has already clocked out today."
            );
        }

        attendance.setCheckOut(LocalTime.now());

        return attendanceRepository.save(attendance);
    }
}