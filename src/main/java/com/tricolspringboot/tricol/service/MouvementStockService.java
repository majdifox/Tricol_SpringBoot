package com.tricolspringboot.tricol.service;

import com.tricolspringboot.tricol.dto.response.MouvementStockResponseDTO;
import com.tricolspringboot.tricol.entity.CommandeFournisseur;
import com.tricolspringboot.tricol.entity.MouvementStock;
import com.tricolspringboot.tricol.entity.Produit;
import com.tricolspringboot.tricol.enums.TypeMouvement;
import com.tricolspringboot.tricol.mapper.MouvementStockMapper;
import com.tricolspringboot.tricol.repository.MouvementStockRepository;
import com.tricolspringboot.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final ProduitRepository produitRepository;
    private final MouvementStockMapper mouvementMapper;
    private final ValoralisationService valorisationService;

    public MouvementStockResponseDTO createMouvement(
            Long produitId,
            TypeMouvement type,
            Integer quantite,
            BigDecimal prixUnitaire,
            CommandeFournisseur commande,
            String commentaire
    ) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + produitId));

        // Validate stock for SORTIE
        if (type == TypeMouvement.SORTIE && produit.getStockActuel() < quantite) {
            throw new IllegalArgumentException(
                    "Stock insuffisant pour le produit " + produit.getNom() +
                            ". Disponible: " + produit.getStockActuel() + ", Demandé: " + quantite
            );
        }

        // Create movement
        MouvementStock mouvement = new MouvementStock();
        mouvement.setDateMouvement(LocalDateTime.now());
        mouvement.setTypeMouvement(type);
        mouvement.setQuantite(quantite);
        mouvement.setPrixUnitaire(prixUnitaire);
        mouvement.setCoutTotal(prixUnitaire.multiply(new BigDecimal(quantite)));
        mouvement.setProduit(produit);
        mouvement.setCommande(commande);
        mouvement.setCommentaire(commentaire);

        // Generate reference
        String reference = generateReference(type, commande);
        mouvement.setReference(reference);

        // Save movement
        MouvementStock saved = mouvementRepository.save(mouvement);

        // Update stock
        updateStockAfterMouvement(produit, type, quantite, prixUnitaire);

        return mouvementMapper.toResponseDTO(saved);
    }

    private void updateStockAfterMouvement(Produit produit, TypeMouvement type, Integer quantite, BigDecimal prixUnitaire) {
        int currentStock = produit.getStockActuel();

        switch (type) {
            case ENTREE:
                produit.setStockActuel(currentStock + quantite);
                // Update CUMP cost
                valorisationService.calculerCoutMoyenPondere(produit.getId(), prixUnitaire, quantite);
                break;

            case SORTIE:
                produit.setStockActuel(currentStock - quantite);
                break;

            case AJUSTEMENT:
                // Ajustement can be positive or negative
                produit.setStockActuel(currentStock + quantite);
                break;
        }

        produitRepository.save(produit);
    }

    private String generateReference(TypeMouvement type, CommandeFournisseur commande) {
        String prefix = switch (type) {
            case ENTREE -> "ENT";
            case SORTIE -> "SRT";
            case AJUSTEMENT -> "ADJ";
        };

        String timestamp = String.valueOf(System.currentTimeMillis());
        String commandeRef = commande != null ? "-CMD" + commande.getId() : "";

        return prefix + "-" + timestamp + commandeRef;
    }

    public Page<MouvementStockResponseDTO> getMouvementsByProduit(Long produitId, Pageable pageable) {
        Page<MouvementStock> mouvements = mouvementRepository.findByProduitId(produitId, pageable);
        return mouvements.map(mouvementMapper::toResponseDTO);
    }

    public List<MouvementStockResponseDTO> getMouvementsByType(TypeMouvement type) {
        List<MouvementStock> mouvements = mouvementRepository.findByTypeMouvement(type);
        return mouvements.stream()
                .map(mouvementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MouvementStockResponseDTO> getMouvementsByCommande(Long commandeId) {
        List<MouvementStock> mouvements = mouvementRepository.findByCommandeId(commandeId);
        return mouvements.stream()
                .map(mouvementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public MouvementStockResponseDTO getMouvementById(Long id) {
        MouvementStock mouvement = mouvementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement non trouvé avec l'ID: " + id));

        return mouvementMapper.toResponseDTO(mouvement);
    }

    // Method used by CommandeService when commande is delivered
    public void createMouvementsForCommande(CommandeFournisseur commande) {
        commande.getQuantites().forEach((produit, quantite) -> {
            createMouvement(
                    produit.getId(),
                    TypeMouvement.ENTREE,
                    quantite,
                    produit.getPrixUnitaire(),
                    commande,
                    "Réception commande fournisseur #" + commande.getId()
            );
        });
    }
}