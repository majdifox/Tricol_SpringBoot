package com.tricolspringboot.tricol.service;

import com.tricolspringboot.tricol.dto.request.CommandeRequestDTO;
import com.tricolspringboot.tricol.dto.response.CommandeResponseDTO;
import com.tricolspringboot.tricol.entity.CommandeFournisseur;
import com.tricolspringboot.tricol.entity.Fournisseur;
import com.tricolspringboot.tricol.entity.Produit;
import com.tricolspringboot.tricol.enums.StatutCommande;
import com.tricolspringboot.tricol.mapper.CommandeMapper;
import com.tricolspringboot.tricol.repository.CommandeFournisseurRepository;
import com.tricolspringboot.tricol.repository.FournisseurRepository;
import com.tricolspringboot.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeFournisseurService {

    private final CommandeFournisseurRepository commandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;
    private final CommandeMapper commandeMapper;
    private final MouvementStockService mouvementStockService;

    public CommandeResponseDTO createCommande(CommandeRequestDTO requestDTO) {
        // Validate fournisseur exists
        Fournisseur fournisseur = fournisseurRepository.findById(requestDTO.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID: " + requestDTO.getFournisseurId()));

        // Validate all products exist
        Map<Long, Integer> produitsQuantites = requestDTO.getProduitsQuantites();
        if (produitsQuantites == null || produitsQuantites.isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un produit");
        }

        // Fetch all products
        List<Produit> produits = produitRepository.findAllById(produitsQuantites.keySet());
        if (produits.size() != produitsQuantites.size()) {
            throw new RuntimeException("Un ou plusieurs produits n'existent pas");
        }

        // Create commande entity
        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setDateCommande(requestDTO.getDateCommande());
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setFournisseur(fournisseur);
        commande.setProduits(produits);

        // Build quantities map (Produit -> Integer)
        Map<Produit, Integer> quantitesMap = new HashMap<>();
        for (Produit produit : produits) {
            Integer quantite = produitsQuantites.get(produit.getId());
            quantitesMap.put(produit, quantite);
        }
        commande.setQuantites(quantitesMap);

        // Calculate montant total
        BigDecimal montantTotal = calculateMontantTotal(produits, produitsQuantites);
        commande.setMontantTotal(montantTotal);

        // Save
        CommandeFournisseur saved = commandeRepository.save(commande);

        return commandeMapper.toResponseDTO(saved);
    }

    private BigDecimal calculateMontantTotal(List<Produit> produits, Map<Long, Integer> quantites) {
        BigDecimal total = BigDecimal.ZERO;

        for (Produit produit : produits) {
            Integer quantite = quantites.get(produit.getId());
            BigDecimal subtotal = produit.getPrixUnitaire().multiply(new BigDecimal(quantite));
            total = total.add(subtotal);
        }

        return total;
    }

    public CommandeResponseDTO getCommandeById(Long id) {
        CommandeFournisseur commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        return commandeMapper.toResponseDTO(commande);
    }

    public Page<CommandeResponseDTO> getAllCommandes(Pageable pageable) {
        Page<CommandeFournisseur> commandes = commandeRepository.findAll(pageable);
        return commandes.map(commandeMapper::toResponseDTO);
    }

    public Page<CommandeResponseDTO> getCommandesByStatut(StatutCommande statut, Pageable pageable) {
        Page<CommandeFournisseur> commandes = commandeRepository.findByStatut(statut, pageable);
        return commandes.map(commandeMapper::toResponseDTO);
    }

    public List<CommandeResponseDTO> getCommandesByFournisseur(Long fournisseurId) {
        List<CommandeFournisseur> commandes = commandeRepository.findByFournisseurId(fournisseurId);
        return commandes.stream()
                .map(commandeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CommandeResponseDTO> getCommandesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<CommandeFournisseur> commandes = commandeRepository.findByDateCommandeBetween(startDate, endDate);
        return commandes.stream()
                .map(commandeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CommandeResponseDTO updateStatut(Long id, StatutCommande newStatut) {
        CommandeFournisseur commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        StatutCommande oldStatut = commande.getStatut();

        // Validate status transition
        validateStatutTransition(oldStatut, newStatut);

        // Update status
        commande.setStatut(newStatut);

        // If changing to LIVREE, create stock movements
        if (newStatut == StatutCommande.LIVREE && oldStatut != StatutCommande.LIVREE) {
            mouvementStockService.createMouvementsForCommande(commande);
        }

        CommandeFournisseur updated = commandeRepository.save(commande);

        return commandeMapper.toResponseDTO(updated);
    }

    private void validateStatutTransition(StatutCommande oldStatut, StatutCommande newStatut) {
        // Business rules for status transitions
        if (oldStatut == StatutCommande.ANNULEE) {
            throw new IllegalStateException("Impossible de modifier une commande annulée");
        }

        if (oldStatut == StatutCommande.LIVREE && newStatut != StatutCommande.LIVREE) {
            throw new IllegalStateException("Impossible de modifier le statut d'une commande déjà livrée");
        }

        // Add more business rules as needed
    }

    public CommandeResponseDTO updateCommande(Long id, CommandeRequestDTO requestDTO) {
        CommandeFournisseur existing = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        // Only allow updates if status is EN_ATTENTE
        if (existing.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de modifier une commande avec le statut: " + existing.getStatut());
        }

        // Update fournisseur if changed
        if (!existing.getFournisseur().getId().equals(requestDTO.getFournisseurId())) {
            Fournisseur newFournisseur = fournisseurRepository.findById(requestDTO.getFournisseurId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
            existing.setFournisseur(newFournisseur);
        }

        // Update products and quantities
        Map<Long, Integer> newQuantites = requestDTO.getProduitsQuantites();
        List<Produit> produits = produitRepository.findAllById(newQuantites.keySet());

        existing.setProduits(produits);

        Map<Produit, Integer> quantitesMap = new HashMap<>();
        for (Produit produit : produits) {
            quantitesMap.put(produit, newQuantites.get(produit.getId()));
        }
        existing.setQuantites(quantitesMap);

        // Recalculate montant total
        BigDecimal newTotal = calculateMontantTotal(produits, newQuantites);
        existing.setMontantTotal(newTotal);

        // Update date
        existing.setDateCommande(requestDTO.getDateCommande());

        CommandeFournisseur updated = commandeRepository.save(existing);

        return commandeMapper.toResponseDTO(updated);
    }

    public void deleteCommande(Long id) {
        CommandeFournisseur commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        // Only allow deletion if EN_ATTENTE
        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de supprimer une commande avec le statut: " + commande.getStatut());
        }

        commandeRepository.deleteById(id);
    }

    public CommandeResponseDTO annulerCommande(Long id) {
        return updateStatut(id, StatutCommande.ANNULEE);
    }

    public CommandeResponseDTO validerCommande(Long id) {
        return updateStatut(id, StatutCommande.VALIDEE);
    }

    public CommandeResponseDTO livrerCommande(Long id) {
        return updateStatut(id, StatutCommande.LIVREE);
    }
}