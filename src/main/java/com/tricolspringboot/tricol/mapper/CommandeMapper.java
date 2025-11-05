package com.tricolspringboot.tricol.mapper;

import com.tricolspringboot.tricol.dto.response.CommandeResponseDTO;
import com.tricolspringboot.tricol.entity.CommandeFournisseur;
import com.tricolspringboot.tricol.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {FournisseurMapper.class, ProduitMapper.class})
public interface CommandeMapper {

    @Mapping(target = "fournisseur", source = "fournisseur")
    @Mapping(target = "produits", source = "produits")
    @Mapping(target = "quantites", expression = "java(mapQuantites(commande))")
    CommandeResponseDTO toResponseDTO(CommandeFournisseur commande);

    // Helper method to convert Map<Produit, Integer> to Map<Long, Integer>
    default Map<Long, Integer> mapQuantites(CommandeFournisseur commande) {
        return commande.getQuantites().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId(),
                        Map.Entry::getValue
                ));
    }
}