package com.tricolspringboot.tricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitResponseDTO {

    private Long id;
    private String nom;
    private String description;
    private BigDecimal prixUnitaire;
    private String categorie;
    private Integer stockActuel;
    private BigDecimal coutMoyenPondere;
}