package com.tricolspringboot.tricol.controller;

import com.tricolspringboot.tricol.dto.request.FournisseurRequestDTO;
import com.tricolspringboot.tricol.dto.response.FournisseurResponseDTO;
import com.tricolspringboot.tricol.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @PostMapping
    public ResponseEntity<FournisseurResponseDTO> createFournisseur(
            @Valid @RequestBody FournisseurRequestDTO requestDTO) {

        FournisseurResponseDTO response = fournisseurService.createFournisseur(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FournisseurResponseDTO> getFournisseurById(@PathVariable Long id) {
        FournisseurResponseDTO response = fournisseurService.getFournisseurById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<FournisseurResponseDTO>> getAllFournisseurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<FournisseurResponseDTO> response = fournisseurService.getAllFournisseurs(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FournisseurResponseDTO> updateFournisseur(
            @PathVariable Long id,
            @Valid @RequestBody FournisseurRequestDTO requestDTO) {

        FournisseurResponseDTO response = fournisseurService.updateFournisseur(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable Long id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.noContent().build();
    }
}