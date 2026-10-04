package com.example.construction.controller;

import com.example.construction.entity.Site;
import com.example.construction.service.ClientAccessService;
import com.example.construction.service.impl.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;
    private final ClientAccessService clientAccessService;

    public SiteController(SiteService siteService,
                          ClientAccessService clientAccessService) {
        this.siteService = siteService;
        this.clientAccessService = clientAccessService;
    }

    @GetMapping
    public ResponseEntity<List<Site>> getAllSites(
            org.springframework.security.core.Authentication authentication) {
        if (isClient(authentication)) {
            if (clientAccessService.getCurrentClient(authentication).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Site> sites = siteService.getAllSites().stream()
                    .filter(site -> clientAccessService.getOwnedProject(
                            authentication, site.getProjectId()).isPresent())
                    .toList();
            return ResponseEntity.ok(sites);
        }

        return ResponseEntity.ok(siteService.getAllSites());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Integer id,
            org.springframework.security.core.Authentication authentication) {
        if (isClient(authentication)) {
            return siteService.getSiteById(id)
                    .filter(site -> clientAccessService.getOwnedProject(
                            authentication, site.getProjectId()).isPresent())
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

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

    private boolean isClient(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT"));
    }
}
