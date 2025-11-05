package com.tricolspringboot.tricol.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitRequestDTO {

    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    private String nom;

    private String description;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal prixUnitaire;

    private String categorie;
}