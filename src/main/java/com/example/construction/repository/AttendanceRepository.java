package com.example.construction.repository;

import com.example.construction.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    Optional<Attendance> findByWorkerIdAndAttendanceDate(
            Integer workerId,
            LocalDate attendanceDate
    );
}