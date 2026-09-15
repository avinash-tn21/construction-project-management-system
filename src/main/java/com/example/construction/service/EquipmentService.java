package com.example.construction.service.impl;

import com.example.construction.entity.Equipment;
import com.example.construction.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Optional<Equipment> getEquipmentById(Integer id) {
        return equipmentRepository.findById(id);
    }

    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Integer id, Equipment equipment) {

        return equipmentRepository.findById(id)
                .map(existingEquipment -> {

                    existingEquipment.setEquipmentName(
                            equipment.getEquipmentName()
                    );

                    existingEquipment.setEquipmentType(
                            equipment.getEquipmentType()
                    );

                    existingEquipment.setModel(
                            equipment.getModel()
                    );

                    existingEquipment.setSerialNumber(
                            equipment.getSerialNumber()
                    );

                    existingEquipment.setPurchaseDate(
                            equipment.getPurchaseDate()
                    );

                    existingEquipment.setStatus(
                            equipment.getStatus()
                    );

                    existingEquipment.setConditionStatus(
                            equipment.getConditionStatus()
                    );

                    existingEquipment.setLocation(
                            equipment.getLocation()
                    );

                    return equipmentRepository.save(existingEquipment);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with ID: " + id
                        )
                );
    }

    public void deleteEquipment(Integer id) {
        equipmentRepository.deleteById(id);
    }
}