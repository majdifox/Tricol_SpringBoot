package com.tricolspringboot.tricol.entity;

import com.tricolspringboot.tricol.enums.StatutCommande;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes_fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date de commande est obligatoire")
    @Column(nullable = false)
    private LocalDate dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    // Relation ManyToOne avec Fournisseur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    @NotNull(message = "Le fournisseur est obligatoire")
    private Fournisseur fournisseur;

    // Relation ManyToMany avec Produit
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "commande_produits",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private List<Produit> produits = new ArrayList<>();

    // Quantités commandées pour chaque produit (stockées séparément)
    @ElementCollection
    @CollectionTable(name = "commande_produit_quantites",
            joinColumns = @JoinColumn(name = "commande_id"))
    @MapKeyJoinColumn(name = "produit_id")
    @Column(name = "quantite")
    private java.util.Map<Produit, Integer> quantites = new java.util.HashMap<>();
}