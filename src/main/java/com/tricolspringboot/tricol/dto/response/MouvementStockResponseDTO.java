package com.tricolspringboot.tricol.dto.response;

import com.tricolspringboot.tricol.enums.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStockResponseDTO {

    private Long id;
    private LocalDateTime dateMouvement;
    private TypeMouvement typeMouvement;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal coutTotal;
    private ProduitResponseDTO produit;
    private Long commandeId;  // Just the ID, not full commande (avoid circular reference)
    private String reference;
    private String commentaire;
}
