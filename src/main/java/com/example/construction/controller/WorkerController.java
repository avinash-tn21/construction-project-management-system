package com.example.construction.controller;

import com.example.construction.entity.Worker;
import com.example.construction.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public List<Worker> getAllWorkers() {
        return workerService.getAllWorkers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Integer id) {
        return workerService.getWorkerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Worker createWorker(@RequestBody Worker worker) {
        return workerService.saveWorker(worker);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Worker> updateWorker(
            @PathVariable Integer id,
            @RequestBody Worker worker) {

        return workerService.getWorkerById(id)
                .map(existingWorker -> {
                    worker.setWorkerId(id);
                    return ResponseEntity.ok(workerService.saveWorker(worker));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Integer id) {
        if (workerService.getWorkerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }
}