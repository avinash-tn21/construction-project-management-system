package com.example.construction.controller;

import com.example.construction.entity.Client;
import com.example.construction.entity.Project;
import com.example.construction.entity.SiteProgress;
import com.example.construction.entity.User;
import com.example.construction.repository.ClientRepository;
import com.example.construction.repository.ProjectRepository;
import com.example.construction.repository.SiteProgressRepository;
import com.example.construction.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
public class ClientDashboardController {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final SiteProgressRepository siteProgressRepository;
    private final UserRepository userRepository;

    public ClientDashboardController(
            ClientRepository clientRepository,
            ProjectRepository projectRepository,
            SiteProgressRepository siteProgressRepository,
            UserRepository userRepository) {

        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
        this.siteProgressRepository = siteProgressRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard(
            Authentication authentication,
            @RequestParam(required = false) Integer projectId,
            Model model) {

        // =====================================================
        // GET CURRENTLY LOGGED-IN USER
        // =====================================================

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null || user.getEmail() == null) {

            model.addAttribute("clientNotFound", true);

            return "client-dashboard";
        }

        // =====================================================
        // FIND CLIENT USING USER EMAIL
        // =====================================================

        Client client = clientRepository
                .findByEmail(user.getEmail())
                .orElse(null);

        if (client == null) {

            model.addAttribute("clientNotFound", true);

            return "client-dashboard";
        }

        // =====================================================
        // FIND PROJECTS BELONGING TO THIS CLIENT
        // =====================================================

        List<Project> projects =
                projectRepository.findByClientId(
                        client.getClientId()
                );

        model.addAttribute("client", client);
        model.addAttribute("projects", projects);

        // =====================================================
        // SELECT PROJECT
        // =====================================================

        Project selectedProject = null;

        if (projectId != null) {

            selectedProject = projects.stream()
                    .filter(project ->
                            project.getProjectId().equals(projectId))
                    .findFirst()
                    .orElse(null);
        }

        // Automatically select first project
        if (selectedProject == null && !projects.isEmpty()) {

            selectedProject = projects.get(0);
        }

        model.addAttribute(
                "selectedProject",
                selectedProject
        );

        // =====================================================
        // GET PROJECT PROGRESS
        // =====================================================

        if (selectedProject != null) {

            List<SiteProgress> progressUpdates =
                    siteProgressRepository
                            .findByProjectIdOrderByProgressDateDescProgressIdDesc(
                                    selectedProject.getProjectId()
                            );

            model.addAttribute(
                    "progressUpdates",
                    progressUpdates
            );

            // =================================================
            // GET LATEST PROGRESS UPDATE
            // =================================================

            SiteProgress latestUpdate =
                    progressUpdates.stream()
                            .filter(progress ->
                                    progress.getProgressPercentage() != null)
                            .findFirst()
                            .orElse(null);

            model.addAttribute(
                    "latestUpdate",
                    latestUpdate
            );

            // =================================================
            // LATEST RECORDED PROGRESS
            // =================================================

            BigDecimal latestProgress = BigDecimal.ZERO;

            if (latestUpdate != null &&
                    latestUpdate.getProgressPercentage() != null) {

                latestProgress =
                        latestUpdate.getProgressPercentage();

                // Keep percentage between 0 and 100
                if (latestProgress.compareTo(
                        BigDecimal.ZERO) < 0) {

                    latestProgress = BigDecimal.ZERO;
                }

                if (latestProgress.compareTo(
                        BigDecimal.valueOf(100)) > 0) {

                    latestProgress =
                            BigDecimal.valueOf(100);
                }
            }

            model.addAttribute(
                    "latestProgress",
                    latestProgress
            );

            // =================================================
            // RECENT UPDATES
            // =================================================

            List<SiteProgress> recentUpdates =
                    progressUpdates.size() > 5
                            ? progressUpdates.subList(0, 5)
                            : progressUpdates;

            model.addAttribute(
                    "recentUpdates",
                    recentUpdates
            );

            model.addAttribute(
                    "noProgress",
                    latestUpdate == null
            );

        } else {

            model.addAttribute(
                    "progressUpdates",
                    Collections.emptyList()
            );

            model.addAttribute(
                    "recentUpdates",
                    Collections.emptyList()
            );

            model.addAttribute(
                    "latestUpdate",
                    null
            );

            model.addAttribute(
                    "latestProgress",
                    BigDecimal.ZERO
            );

            model.addAttribute(
                    "noProgress",
                    true
            );
        }

        return "client-dashboard";
    }
}