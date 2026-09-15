package com.example.construction.controller;

import com.example.construction.entity.Contractor;
import com.example.construction.repository.ContractorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contractors")
public class ContractorController {

    private final ContractorRepository contractorRepository;

    public ContractorController(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    // READ ALL
    @GetMapping
    public List<Contractor> getAllContractors() {
        return contractorRepository.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Contractor> getContractorById(@PathVariable Integer id) {

        return contractorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public Contractor createContractor(@RequestBody Contractor contractor) {
        return contractorRepository.save(contractor);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Contractor> updateContractor(
            @PathVariable Integer id,
            @RequestBody Contractor updatedContractor) {

        return contractorRepository.findById(id)
                .map(contractor -> {

                    contractor.setContractorName(
                            updatedContractor.getContractorName());

                    contractor.setPhone(
                            updatedContractor.getPhone());

                    contractor.setEmail(
                            updatedContractor.getEmail());

                    contractor.setAddress(
                            updatedContractor.getAddress());

                    return ResponseEntity.ok(
                            contractorRepository.save(contractor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContractor(
            @PathVariable Integer id) {

        if (!contractorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        contractorRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}