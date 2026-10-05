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

        // Get the currently logged-in user
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null || user.getEmail() == null) {
            model.addAttribute("clientNotFound", true);
            return "client-dashboard";
        }

        // Match the user's email with the client profile
        Client client = clientRepository.findByEmail(user.getEmail())
                .orElse(null);

        if (client == null) {
            model.addAttribute("clientNotFound", true);
            return "client-dashboard";
        }

        // Find projects belonging to this client
        List<Project> projects =
                projectRepository.findByClientId(
                        client.getClientId()
                );

        model.addAttribute("client", client);
        model.addAttribute("projects", projects);

        Project selectedProject = null;

        // Check whether a project was selected
        if (projectId != null) {

            selectedProject = projects.stream()
                    .filter(project ->
                            project.getProjectId().equals(projectId))
                    .findFirst()
                    .orElse(null);
        }

        // Automatically select the first project
        if (selectedProject == null && !projects.isEmpty()) {
            selectedProject = projects.get(0);
        }

        model.addAttribute("selectedProject", selectedProject);

        if (selectedProject != null) {

            // Retrieve progress updates for the selected project
            List<SiteProgress> progressUpdates =
                    siteProgressRepository
                            .findByProjectIdOrderByProgressDateDesc(
                                    selectedProject.getProjectId()
                            );

            model.addAttribute(
                    "progressUpdates",
                    progressUpdates
            );

            // =====================================================
            // CALCULATE OVERALL PROJECT PROGRESS
            // =====================================================
            //
            // Each progress percentage is treated as an
            // incremental progress value.
            //
            // Example:
            // 2% + 11% + 26% = 39%
            //
            // The result is capped at 100%.
            // =====================================================

            if (progressUpdates.isEmpty()) {

                model.addAttribute(
                        "overallProgress",
                        BigDecimal.ZERO
                );

                model.addAttribute(
                        "noProgress",
                        true
                );

            } else {

                BigDecimal overallProgress =
                        progressUpdates.stream()
                                .map(SiteProgress::getProgressPercentage)
                                .filter(progress ->
                                        progress != null)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add
                                );

                // Do not allow progress to exceed 100%
                if (overallProgress.compareTo(
                        BigDecimal.valueOf(100)) > 0) {

                    overallProgress =
                            BigDecimal.valueOf(100);
                }

                model.addAttribute(
                        "overallProgress",
                        overallProgress
                );

                model.addAttribute(
                        "noProgress",
                        false
                );
            }

            // =====================================================
            // DISPLAY FIVE LATEST PROGRESS UPDATES
            // =====================================================

            List<SiteProgress> recentUpdates =
                    progressUpdates.size() > 5
                            ? progressUpdates.subList(0, 5)
                            : progressUpdates;

            model.addAttribute(
                    "recentUpdates",
                    recentUpdates
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
                    "overallProgress",
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