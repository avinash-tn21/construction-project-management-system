package com.example.construction.service.impl;

import com.example.construction.entity.MaterialRequest;
import com.example.construction.repository.MaterialRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialRequestService {

    private final MaterialRequestRepository requestRepository;

    public MaterialRequestService(MaterialRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<MaterialRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<MaterialRequest> getRequestById(Integer id) {
        return requestRepository.findById(id);
    }

    public MaterialRequest saveRequest(MaterialRequest request) {
        return requestRepository.save(request);
    }

    public MaterialRequest updateRequest(
            Integer id,
            MaterialRequest request) {

        return requestRepository.findById(id)
                .map(existing -> {

                    existing.setProject(request.getProject());
                    existing.setInventoryItem(request.getInventoryItem());
                    existing.setRequestedBy(request.getRequestedBy());
                    existing.setQuantity(request.getQuantity());
                    existing.setRequestDate(request.getRequestDate());
                    existing.setStatus(request.getStatus());
                    existing.setRemarks(request.getRemarks());

                    return requestRepository.save(existing);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Material request not found with ID: " + id));
    }

    public void deleteRequest(Integer id) {
        requestRepository.deleteById(id);
    }
}