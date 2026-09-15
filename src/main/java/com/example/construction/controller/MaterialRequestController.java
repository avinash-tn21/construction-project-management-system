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

    @GetMapping
    public List<MaterialRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialRequest> getRequestById(
            @PathVariable Integer id) {

        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MaterialRequest> createRequest(
            @RequestBody MaterialRequest request) {

        return ResponseEntity.ok(
                requestService.saveRequest(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialRequest> updateRequest(
            @PathVariable Integer id,
            @RequestBody MaterialRequest request) {

        try {
            return ResponseEntity.ok(
                    requestService.updateRequest(id, request)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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