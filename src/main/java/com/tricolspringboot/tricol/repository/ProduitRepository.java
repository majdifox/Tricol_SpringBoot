package com.tricolspringboot.tricol.repository;

import com.tricolspringboot.tricol.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    // Find products by category with pagination
    Page<Produit> findByCategorie(String categorie, Pageable pageable);

    // Find products by name containing (case-insensitive search)
    List<Produit> findByNomContainingIgnoreCase(String nom);

    // Find products with low stock
    List<Produit> findByStockActuelLessThan(Integer seuil);
}