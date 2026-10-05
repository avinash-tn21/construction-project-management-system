package com.example.construction.controller;

import com.example.construction.entity.MaterialRequest;
import com.example.construction.service.impl.MaterialRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material-requests")
public class MaterialRequestController {

    private final MaterialRequestService requestService;

    public MaterialRequestController(
            MaterialRequestService requestService) {

        this.requestService = requestService;
    }


    // =========================
    // GET ALL REQUESTS
    // =========================

    @GetMapping
    public List<MaterialRequest> getAllRequests() {

        return requestService.getAllRequests();
    }


    // =========================
    // GET REQUEST BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<MaterialRequest> getRequestById(
            @PathVariable Integer id) {

        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // =========================
    // CREATE REQUEST
    // =========================

    @PostMapping
    public ResponseEntity<MaterialRequest> createRequest(
            @RequestBody MaterialRequest request) {

        MaterialRequest savedRequest =
                requestService.saveRequest(request);

        return ResponseEntity.ok(savedRequest);
    }


    // =========================
    // UPDATE REQUEST
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<MaterialRequest> updateRequest(
            @PathVariable Integer id,
            @RequestBody MaterialRequest request) {

        try {

            MaterialRequest updatedRequest =
                    requestService.updateRequest(id, request);

            return ResponseEntity.ok(updatedRequest);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }


    // =========================
    // DELETE REQUEST
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Integer id) {

        if (requestService.getRequestById(id).isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        requestService.deleteRequest(id);

        return ResponseEntity.noContent().build();
    }
}