package com.tricolspringboot.tricol.entity;

import com.tricolspringboot.tricol.enums.TypeMouvement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date du mouvement est obligatoire")
    @Column(nullable = false)
    private LocalDateTime dateMouvement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement typeMouvement;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column(nullable = false)
    private Integer quantite;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixUnitaire;  // Prix au moment du mouvement

    @Column(precision = 12, scale = 2)
    private BigDecimal coutTotal;  // quantite * prixUnitaire

    // Relation ManyToOne avec Produit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @NotNull(message = "Le produit est obligatoire")
    private Produit produit;

    // Relation ManyToOne avec CommandeFournisseur (optionnel pour AJUSTEMENT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    private CommandeFournisseur commande;

    private String reference;  // Référence du mouvement (ex: "CMD-2025-001")

    @Column(columnDefinition = "TEXT")
    private String commentaire;  // Notes additionnelles
}