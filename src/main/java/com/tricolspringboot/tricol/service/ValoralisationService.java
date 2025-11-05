package com.tricolspringboot.tricol.service;

import com.tricolspringboot.tricol.entity.MouvementStock;
import com.tricolspringboot.tricol.entity.Produit;
import com.tricolspringboot.tricol.enums.MethodeValorisation;
import com.tricolspringboot.tricol.enums.TypeMouvement;
import com.tricolspringboot.tricol.repository.MouvementStockRepository;
import com.tricolspringboot.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ValoralisationService {

    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementRepository;

    @Value("${app.valorisation.method:CUMP}")
    private String methodeName;

    public void calculerCoutMoyenPondere(Long produitId, BigDecimal nouveauPrixUnitaire, Integer nouvelleQuantite) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        MethodeValorisation methode = MethodeValorisation.valueOf(methodeName);

        if (methode == MethodeValorisation.CUMP) {
            calculerCUMP(produit, nouveauPrixUnitaire, nouvelleQuantite);
        } else if (methode == MethodeValorisation.FIFO) {
            // FIFO: No average cost calculation, use actual movement prices
            // Cost is determined at the time of exit
        }
    }

    private void calculerCUMP(Produit produit, BigDecimal nouveauPrixUnitaire, Integer nouvelleQuantite) {
        // Formula: CUMP = (Old Stock Value + New Purchase Value) / (Old Stock + New Stock)

        BigDecimal ancienStock = new BigDecimal(produit.getStockActuel());
        BigDecimal ancienCout = produit.getCoutMoyenPondere();
        BigDecimal nouvelleQte = new BigDecimal(nouvelleQuantite);

        // Old stock value
        BigDecimal valeurAncienStock = ancienStock.multiply(ancienCout);

        // New purchase value
        BigDecimal valeurNouvelAchat = nouvelleQte.multiply(nouveauPrixUnitaire);

        // Total value
        BigDecimal valeurTotale = valeurAncienStock.add(valeurNouvelAchat);

        // Total quantity
        BigDecimal quantiteTotale = ancienStock.add(nouvelleQte);

        // New average cost
        BigDecimal nouveauCUMP = BigDecimal.ZERO;
        if (quantiteTotale.compareTo(BigDecimal.ZERO) > 0) {
            nouveauCUMP = valeurTotale.divide(quantiteTotale, 2, RoundingMode.HALF_UP);
        }

        produit.setCoutMoyenPondere(nouveauCUMP);
        produitRepository.save(produit);
    }

    public BigDecimal calculerCoutSortieFIFO(Long produitId, Integer quantiteSortie) {
        // Get all ENTREE movements for this product, ordered by date (oldest first)
        List<MouvementStock> entrees = mouvementRepository.findEntreesForFifo(produitId);

        BigDecimal coutTotal = BigDecimal.ZERO;
        int quantiteRestante = quantiteSortie;

        for (MouvementStock entree : entrees) {
            if (quantiteRestante <= 0) break;

            int quantiteDisponible = entree.getQuantite();
            int quantiteAUtiliser = Math.min(quantiteRestante, quantiteDisponible);

            BigDecimal coutPartiel = entree.getPrixUnitaire()
                    .multiply(new BigDecimal(quantiteAUtiliser));

            coutTotal = coutTotal.add(coutPartiel);
            quantiteRestante -= quantiteAUtiliser;
        }

        return coutTotal;
    }
}