package com.tricolspringboot.tricol.service;

import com.tricolspringboot.tricol.dto.request.FournisseurRequestDTO;
import com.tricolspringboot.tricol.dto.response.FournisseurResponseDTO;
import com.tricolspringboot.tricol.entity.Fournisseur;
import com.tricolspringboot.tricol.exception.ResourceNotFoundException;
import com.tricolspringboot.tricol.mapper.FournisseurMapper;
import com.tricolspringboot.tricol.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;

    public FournisseurResponseDTO createFournisseur(FournisseurRequestDTO requestDTO) {
        // Check if ICE already exists
        if (requestDTO.getIce() != null && fournisseurRepository.existsByIce(requestDTO.getIce())) {
            throw new IllegalArgumentException("Un fournisseur avec cet ICE existe déjà");
        }

        // Convert DTO to Entity
        Fournisseur fournisseur = fournisseurMapper.toEntity(requestDTO);

        // Save to database
        Fournisseur saved = fournisseurRepository.save(fournisseur);

        // Convert Entity to Response DTO and return
        return fournisseurMapper.toResponseDTO(saved);
    }

    public FournisseurResponseDTO getFournisseurById(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID: " + id));

        return fournisseurMapper.toResponseDTO(fournisseur);
    }

    public Page<FournisseurResponseDTO> getAllFournisseurs(Pageable pageable) {
        Page<Fournisseur> fournisseurs = fournisseurRepository.findAll(pageable);
        return fournisseurs.map(fournisseurMapper::toResponseDTO);
    }

    public FournisseurResponseDTO updateFournisseur(Long id, FournisseurRequestDTO requestDTO) {
        Fournisseur existing = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID: " + id));

        // Check ICE uniqueness if it's being changed
        if (requestDTO.getIce() != null && !requestDTO.getIce().equals(existing.getIce())) {
            if (fournisseurRepository.existsByIce(requestDTO.getIce())) {
                throw new IllegalArgumentException("Un fournisseur avec cet ICE existe déjà");
            }
        }

        // Update entity
        fournisseurMapper.updateEntityFromDTO(requestDTO, existing);

        // Save
        Fournisseur updated = fournisseurRepository.save(existing);

        return fournisseurMapper.toResponseDTO(updated);
    }

    public void deleteFournisseur(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fournisseur", "ID", id);

        }

        fournisseurRepository.deleteById(id);
    }
}