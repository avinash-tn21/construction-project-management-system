 
        package com.example.construction.controller;

import com.example.construction.entity.Attendance;
import com.example.construction.entity.Equipment;
import com.example.construction.entity.Project;
import com.example.construction.entity.Site;
import com.example.construction.entity.SiteProgress;
import com.example.construction.entity.Worker;
import com.example.construction.repository.AttendanceRepository;
import com.example.construction.repository.EquipmentRepository;
import com.example.construction.repository.ProjectRepository;
import com.example.construction.repository.SiteProgressRepository;
import com.example.construction.repository.SiteRepository;
import com.example.construction.repository.WorkerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final ProjectRepository projectRepository;
    private final SiteRepository siteRepository;
    private final WorkerRepository workerRepository;
    private final EquipmentRepository equipmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final SiteProgressRepository siteProgressRepository;

    public DashboardController(
            ProjectRepository projectRepository,
            SiteRepository siteRepository,
            WorkerRepository workerRepository,
            EquipmentRepository equipmentRepository,
            AttendanceRepository attendanceRepository,
            SiteProgressRepository siteProgressRepository) {

        this.projectRepository = projectRepository;
        this.siteRepository = siteRepository;
        this.workerRepository = workerRepository;
        this.equipmentRepository = equipmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.siteProgressRepository = siteProgressRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // =========================
        // PROJECTS
        // =========================

        List<Project> projects = projectRepository.findAll();

        long activeProjects = projects.stream()
                .filter(project ->
                        project.getStatus() != null
                                && (
                                project.getStatus().equalsIgnoreCase("ACTIVE")
                                        || project.getStatus().equalsIgnoreCase("IN PROGRESS")
                                        || project.getStatus().equalsIgnoreCase("ONGOING")
                        )
                )
                .count();

        model.addAttribute("activeProjects", activeProjects);
        model.addAttribute("totalProjects", projects.size());


        // =========================
        // SITES
        // =========================

        List<Site> sites = siteRepository.findAll();

        long activeSites = sites.stream()
                .filter(site ->
                        site.getStatus() != null
                                && (
                                site.getStatus().equalsIgnoreCase("ACTIVE")
                                        || site.getStatus().equalsIgnoreCase("OPERATIONAL")
                                        || site.getStatus().equalsIgnoreCase("IN PROGRESS")
                        )
                )
                .count();

        model.addAttribute("activeSites", activeSites);
        model.addAttribute("totalSites", sites.size());


        // =========================
        // WORKERS
        // =========================

        List<Worker> workers = workerRepository.findAll();

        long totalWorkers = workers.size();

        model.addAttribute("totalWorkers", totalWorkers);


        // =========================
        // TODAY'S ATTENDANCE
        // =========================

        LocalDate today = LocalDate.now();

        long presentToday = 0;
        long absentToday = 0;

        for (Worker worker : workers) {

            Attendance attendance =
                    attendanceRepository
                            .findByWorkerIdAndAttendanceDate(
                                    worker.getWorkerId(),
                                    today
                            )
                            .orElse(null);

            if (attendance != null
                    && attendance.getStatus() != null) {

                String status =
                        attendance.getStatus().trim().toUpperCase();

                if (status.equals("PRESENT")
                        || status.equals("CHECKED_IN")
                        || status.equals("CHECKED IN")) {

                    presentToday++;

                } else if (status.equals("ABSENT")) {

                    absentToday++;
                }
            }
        }

        model.addAttribute("presentToday", presentToday);
        model.addAttribute("absentToday", absentToday);


        // =========================
        // EQUIPMENT
        // =========================

        List<Equipment> equipment =
                equipmentRepository.findAll();

        long totalEquipment = equipment.size();

        long availableEquipment = equipment.stream()
                .filter(item ->
                        item.getStatus() != null
                                && item.getStatus()
                                .equalsIgnoreCase("AVAILABLE")
                )
                .count();

        long allocatedEquipment = equipment.stream()
                .filter(item ->
                        item.getStatus() != null
                                && (
                                item.getStatus()
                                        .equalsIgnoreCase("IN USE")
                                        || item.getStatus()
                                        .equalsIgnoreCase("ALLOCATED")
                        )
                )
                .count();

        model.addAttribute("totalEquipment", totalEquipment);
        model.addAttribute("availableEquipment", availableEquipment);
        model.addAttribute("allocatedEquipment", allocatedEquipment);


        // =========================
        // PROJECT PROGRESS
        // =========================

        List<ProjectDashboardData> projectDashboardData =
                projects.stream()
                        .map(project -> {

                            List<SiteProgress> progressList =
                                    siteProgressRepository
                                            .findByProjectIdOrderByProgressDateDesc(
                                                    project.getProjectId()
                                            );

                            int progress = 0;

                            if (!progressList.isEmpty()
                                    && progressList.get(0)
                                    .getProgressPercentage() != null) {

                                progress =
                                        progressList.get(0)
                                                .getProgressPercentage()
                                                .intValue();
                            }

                            return new ProjectDashboardData(
                                    project,
                                    progress
                            );
                        })
                        .toList();

        model.addAttribute(
                "projectDashboardData",
                projectDashboardData
        );


        // =========================
        // RECENT SITE ACTIVITIES
        // =========================

        List<SiteProgress> recentProgress =
                siteProgressRepository.findAll()
                        .stream()
                        .filter(progress ->
                                progress.getProgressDate() != null
                        )
                        .sorted((a, b) ->
                                b.getProgressDate()
                                        .compareTo(a.getProgressDate())
                        )
                        .limit(5)
                        .toList();

        model.addAttribute(
                "recentProgress",
                recentProgress
        );


        return "dashboard";
    }


    // ==========================================
    // DASHBOARD PROJECT DATA
    // ==========================================

    public static class ProjectDashboardData {

        private final Project project;
        private final int progress;

        public ProjectDashboardData(
                Project project,
                int progress) {

            this.project = project;
            this.progress = progress;
        }

        public Project getProject() {
            return project;
        }

        public int getProgress() {
            return progress;
        }
    }
}

