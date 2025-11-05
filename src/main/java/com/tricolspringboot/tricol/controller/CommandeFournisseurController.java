package com.tricolspringboot.tricol.controller;

import com.tricolspringboot.tricol.dto.request.CommandeRequestDTO;
import com.tricolspringboot.tricol.dto.response.CommandeResponseDTO;
import com.tricolspringboot.tricol.enums.StatutCommande;
import com.tricolspringboot.tricol.service.CommandeFournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeService;

    @PostMapping
    public ResponseEntity<CommandeResponseDTO> createCommande(
            @Valid @RequestBody CommandeRequestDTO requestDTO) {

        CommandeResponseDTO response = commandeService.createCommande(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponseDTO> getCommandeById(@PathVariable Long id) {
        CommandeResponseDTO response = commandeService.getCommandeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CommandeResponseDTO>> getAllCommandes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCommande") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<CommandeResponseDTO> response = commandeService.getAllCommandes(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<Page<CommandeResponseDTO>> getCommandesByStatut(
            @PathVariable StatutCommande statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCommande"));
        Page<CommandeResponseDTO> response = commandeService.getCommandesByStatut(statut, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    public ResponseEntity<List<CommandeResponseDTO>> getCommandesByFournisseur(
            @PathVariable Long fournisseurId) {

        List<CommandeResponseDTO> response = commandeService.getCommandesByFournisseur(fournisseurId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CommandeResponseDTO>> getCommandesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<CommandeResponseDTO> response = commandeService.getCommandesByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeResponseDTO> updateCommande(
            @PathVariable Long id,
            @Valid @RequestBody CommandeRequestDTO requestDTO) {

        CommandeResponseDTO response = commandeService.updateCommande(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<CommandeResponseDTO> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutCommande statut) {

        CommandeResponseDTO response = commandeService.updateStatut(id, statut);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<CommandeResponseDTO> annulerCommande(@PathVariable Long id) {
        CommandeResponseDTO response = commandeService.annulerCommande(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/valider")
    public ResponseEntity<CommandeResponseDTO> validerCommande(@PathVariable Long id) {
        CommandeResponseDTO response = commandeService.validerCommande(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/livrer")
    public ResponseEntity<CommandeResponseDTO> livrerCommande(@PathVariable Long id) {
        CommandeResponseDTO response = commandeService.livrerCommande(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}