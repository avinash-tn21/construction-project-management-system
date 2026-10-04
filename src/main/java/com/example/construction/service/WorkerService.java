package com.example.construction.service;

import com.example.construction.entity.Worker;
import com.example.construction.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;

    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Optional<Worker> getWorkerById(Integer id) {
        return workerRepository.findById(id);
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public void deleteWorker(Integer id) {
        workerRepository.deleteById(id);
    }
}