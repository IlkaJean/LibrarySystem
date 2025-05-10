package com.library.controller;

import com.library.dto.SponsorDto;
import com.library.model.Sponsor;
import com.library.repository.SponsorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sponsors")
public class SponsorController {

    @Autowired
    private SponsorRepository sponsorRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<SponsorDto> getAllSponsors(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sponsorRepository.findAll(pageable).map(this::convertToDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<SponsorDto> getSponsor(@PathVariable Long id) {
        return sponsorRepository.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSponsor(@PathVariable Long id) {
        sponsorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private SponsorDto convertToDto(Sponsor sponsor) {
        SponsorDto dto = new SponsorDto();
        BeanUtils.copyProperties(sponsor, dto);
        return dto;
    }
}