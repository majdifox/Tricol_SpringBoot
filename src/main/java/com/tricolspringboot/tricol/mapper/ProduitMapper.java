package com.tricolspringboot.tricol.mapper;

import com.tricolspringboot.tricol.dto.request.ProduitRequestDTO;
import com.tricolspringboot.tricol.dto.response.ProduitResponseDTO;
import com.tricolspringboot.tricol.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitResponseDTO toResponseDTO(Produit produit);

    Produit toEntity(ProduitRequestDTO requestDTO);

    void updateEntityFromDTO(ProduitRequestDTO requestDTO, @MappingTarget Produit produit);
}