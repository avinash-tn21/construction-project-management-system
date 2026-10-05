package com.example.construction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DailySiteLogPageController {

    @GetMapping("/daily-logs")
    public String dailyLogsPage() {
        return "daily-logs";
    }
}