package com.tricolspringboot.tricol.controller;

import com.tricolspringboot.tricol.dto.response.MouvementStockResponseDTO;
import com.tricolspringboot.tricol.enums.TypeMouvement;
import com.tricolspringboot.tricol.service.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementService;

    @GetMapping("/{id}")
    public ResponseEntity<MouvementStockResponseDTO> getMouvementById(@PathVariable Long id) {
        MouvementStockResponseDTO response = mouvementService.getMouvementById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/produit/{produitId}")
    public ResponseEntity<Page<MouvementStockResponseDTO>> getMouvementsByProduit(
            @PathVariable Long produitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateMouvement"));
        Page<MouvementStockResponseDTO> response = mouvementService.getMouvementsByProduit(produitId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsByType(
            @PathVariable TypeMouvement type) {

        List<MouvementStockResponseDTO> response = mouvementService.getMouvementsByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<MouvementStockResponseDTO>> getMouvementsByCommande(
            @PathVariable Long commandeId) {

        List<MouvementStockResponseDTO> response = mouvementService.getMouvementsByCommande(commandeId);
        return ResponseEntity.ok(response);
    }
}