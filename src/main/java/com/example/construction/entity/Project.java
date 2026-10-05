package com.example.construction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @NotBlank(message = "Project name is required.")
    @Size(min = 3, max = 150,
            message = "Project name must be between 3 and 150 characters.")
    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Size(max = 1000,
            message = "Description cannot exceed 1000 characters.")
    private String description;

    @NotBlank(message = "Location is required.")
    @Size(max = 200,
            message = "Location cannot exceed 200 characters.")
    private String location;

    @NotNull(message = "Start date is required.")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    @Column(name = "end_date")
    private LocalDate endDate;

    @NotBlank(message = "Project status is required.")
    @Column(nullable = false)
    private String status;

    @NotNull(message = "Client is required.")
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "contractor_id")
    private Integer contractorId;

    public Project() {
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getContractorId() {
        return contractorId;
    }

    public void setContractorId(Integer contractorId) {
        this.contractorId = contractorId;
    }
}