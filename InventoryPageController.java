package com.example.construction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventoryPageController {

    @GetMapping("/inventory")
    public String inventory() {
        return "inventory";
    }
}