package com.tricolspringboot.tricol.dto.response;

import com.tricolspringboot.tricol.enums.StatutCommande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeResponseDTO {

    private Long id;
    private LocalDate dateCommande;
    private StatutCommande statut;
    private BigDecimal montantTotal;
    private FournisseurResponseDTO fournisseur;  // Full fournisseur info
    private List<ProduitResponseDTO> produits;    // Full product info
    private Map<Long, Integer> quantites;         // Map<produitId, quantite>
}