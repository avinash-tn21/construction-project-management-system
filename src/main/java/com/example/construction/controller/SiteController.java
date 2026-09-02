package com.example.construction.controller;

import com.example.construction.entity.Site;
import com.example.construction.service.impl.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public List<Site> getAllSites() {
        return siteService.getAllSites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Integer id) {
        return siteService.getSiteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Site createSite(@RequestBody Site site) {
        return siteService.createSite(site);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Site> updateSite(
            @PathVariable Integer id,
            @RequestBody Site site) {

        try {
            return ResponseEntity.ok(siteService.updateSite(id, site));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Integer id) {
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}