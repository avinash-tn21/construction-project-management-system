package com.example.construction.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "milestone_id")
    private Integer milestoneId;

    @Column(name = "milestone_name", nullable = false)
    private String milestoneName;

    private String description;

    @Column(name = "milestone_date", nullable = false)
    private LocalDate milestoneDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    public Milestone() {
    }

    public Integer getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(Integer milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(LocalDate milestoneDate) {
        this.milestoneDate = milestoneDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
}