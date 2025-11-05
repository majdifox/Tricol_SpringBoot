package com.tricolspringboot.tricol.mapper;

import com.tricolspringboot.tricol.dto.response.MouvementStockResponseDTO;
import com.tricolspringboot.tricol.entity.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProduitMapper.class})
public interface MouvementStockMapper {

    @Mapping(target = "commandeId", source = "commande.id")
    @Mapping(target = "produit", source = "produit")
    MouvementStockResponseDTO toResponseDTO(MouvementStock mouvement);
}