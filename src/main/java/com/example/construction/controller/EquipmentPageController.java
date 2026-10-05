package com.example.construction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EquipmentPageController {

    @GetMapping("/equipment")
    public String equipment() {
        return "equipment";
    }
}