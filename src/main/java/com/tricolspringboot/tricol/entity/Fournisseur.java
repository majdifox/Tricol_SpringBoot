package com.tricolspringboot.tricol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La société ne peut pas être vide")
    @Column(nullable = false)
    private String societe;

    private String adresse;

    private String contact;

    @Email(message = "Email invalide")
    private String email;

    private String telephone;

    private String ville;

    @Column(unique = true)
    private String ice;  // Identifiant Commun de l'Entreprise (Morocco specific)
}