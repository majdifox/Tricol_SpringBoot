package com.tricolspringboot.tricol.service;

import com.tricolspringboot.tricol.dto.request.ProduitRequestDTO;
import com.tricolspringboot.tricol.dto.response.ProduitResponseDTO;
import com.tricolspringboot.tricol.entity.Produit;
import com.tricolspringboot.tricol.mapper.ProduitMapper;
import com.tricolspringboot.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    public ProduitResponseDTO createProduit(ProduitRequestDTO requestDTO) {
        Produit produit = produitMapper.toEntity(requestDTO);

        // Initialize stock fields
        produit.setStockActuel(0);
        produit.setCoutMoyenPondere(BigDecimal.ZERO);

        Produit saved = produitRepository.save(produit);
        return produitMapper.toResponseDTO(saved);
    }

    public ProduitResponseDTO getProduitById(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        return produitMapper.toResponseDTO(produit);
    }

    public Page<ProduitResponseDTO> getAllProduits(Pageable pageable) {
        Page<Produit> produits = produitRepository.findAll(pageable);
        return produits.map(produitMapper::toResponseDTO);
    }

    public Page<ProduitResponseDTO> getProduitsByCategorie(String categorie, Pageable pageable) {
        Page<Produit> produits = produitRepository.findByCategorie(categorie, pageable);
        return produits.map(produitMapper::toResponseDTO);
    }

    public List<ProduitResponseDTO> searchProduitsByNom(String nom) {
        List<Produit> produits = produitRepository.findByNomContainingIgnoreCase(nom);
        return produits.stream()
                .map(produitMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProduitResponseDTO> getProduitsWithLowStock(Integer seuil) {
        List<Produit> produits = produitRepository.findByStockActuelLessThan(seuil);
        return produits.stream()
                .map(produitMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProduitResponseDTO updateProduit(Long id, ProduitRequestDTO requestDTO) {
        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        // Update only editable fields (not stock or cost)
        produitMapper.updateEntityFromDTO(requestDTO, existing);

        Produit updated = produitRepository.save(existing);
        return produitMapper.toResponseDTO(updated);
    }

    public void deleteProduit(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
        }

        produitRepository.deleteById(id);
    }

    // Method used internally by other services
    public void updateStock(Long produitId, Integer quantite) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        produit.setStockActuel(produit.getStockActuel() + quantite);
        produitRepository.save(produit);
    }
}