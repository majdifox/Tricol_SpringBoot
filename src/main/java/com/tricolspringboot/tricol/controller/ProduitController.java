package com.tricolspringboot.tricol.controller;

import com.tricolspringboot.tricol.dto.request.ProduitRequestDTO;
import com.tricolspringboot.tricol.dto.response.ProduitResponseDTO;
import com.tricolspringboot.tricol.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    public ResponseEntity<ProduitResponseDTO> createProduit(
            @Valid @RequestBody ProduitRequestDTO requestDTO) {

        ProduitResponseDTO response = produitService.createProduit(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> getProduitById(@PathVariable Long id) {
        ProduitResponseDTO response = produitService.getProduitById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProduitResponseDTO>> getAllProduits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProduitResponseDTO> response = produitService.getAllProduits(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<Page<ProduitResponseDTO>> getProduitsByCategorie(
            @PathVariable String categorie,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProduitResponseDTO> response = produitService.getProduitsByCategorie(categorie, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProduitResponseDTO>> searchProduitsByNom(
            @RequestParam String nom) {

        List<ProduitResponseDTO> response = produitService.searchProduitsByNom(nom);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProduitResponseDTO>> getProduitsWithLowStock(
            @RequestParam(defaultValue = "10") Integer seuil) {

        List<ProduitResponseDTO> response = produitService.getProduitsWithLowStock(seuil);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> updateProduit(
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequestDTO requestDTO) {

        ProduitResponseDTO response = produitService.updateProduit(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
}