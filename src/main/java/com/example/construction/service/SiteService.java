package com.example.construction.service.impl;

import com.example.construction.entity.Site;
import com.example.construction.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SiteService {

    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Optional<Site> getSiteById(Integer id) {
        return siteRepository.findById(id);
    }

    public Site createSite(Site site) {
        return siteRepository.save(site);
    }

    public Site updateSite(Integer id, Site updatedSite) {
        return siteRepository.findById(id)
                .map(existingSite -> {
                    existingSite.setProjectId(updatedSite.getProjectId());
                    existingSite.setSiteName(updatedSite.getSiteName());
                    existingSite.setLocation(updatedSite.getLocation());
                    existingSite.setStatus(updatedSite.getStatus());

                    return siteRepository.save(existingSite);
                })
                .orElseThrow(() -> new RuntimeException("Site not found"));
    }

    public void deleteSite(Integer id) {
        siteRepository.deleteById(id);
    }
}