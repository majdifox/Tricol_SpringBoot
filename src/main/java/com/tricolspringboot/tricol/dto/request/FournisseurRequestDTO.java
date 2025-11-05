package com.tricolspringboot.tricol.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurRequestDTO {

    @NotBlank(message = "La société ne peut pas être vide")
    private String societe;

    private String adresse;

    private String contact;

    @Email(message = "Email invalide")
    private String email;

    private String telephone;

    private String ville;

    private String ice;
}