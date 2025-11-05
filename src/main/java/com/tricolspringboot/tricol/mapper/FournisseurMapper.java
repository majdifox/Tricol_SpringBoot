package com.tricolspringboot.tricol.mapper;

import com.tricolspringboot.tricol.dto.request.FournisseurRequestDTO;
import com.tricolspringboot.tricol.dto.response.FournisseurResponseDTO;
import com.tricolspringboot.tricol.entity.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {

    // Convert Entity to Response DTO
    FournisseurResponseDTO toResponseDTO(Fournisseur fournisseur);

    // Convert Request DTO to Entity
    Fournisseur toEntity(FournisseurRequestDTO requestDTO);

    // Update existing entity from Request DTO (for PUT/PATCH)
    void updateEntityFromDTO(FournisseurRequestDTO requestDTO, @MappingTarget Fournisseur fournisseur);
}