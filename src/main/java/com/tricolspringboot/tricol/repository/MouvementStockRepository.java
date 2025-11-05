package com.tricolspringboot.tricol.repository;

import com.tricolspringboot.tricol.entity.MouvementStock;
import com.tricolspringboot.tricol.enums.TypeMouvement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    // Find movements by product ID with pagination
    Page<MouvementStock> findByProduitId(Long produitId, Pageable pageable);

    // Find movements by type
    List<MouvementStock> findByTypeMouvement(TypeMouvement type);

    // Find movements by commande
    List<MouvementStock> findByCommandeId(Long commandeId);

    // Custom query: Get all ENTREE movements for FIFO calculation
    @Query("SELECT m FROM MouvementStock m WHERE m.produit.id = :produitId AND m.typeMouvement = 'ENTREE' ORDER BY m.dateMouvement ASC")
    List<MouvementStock> findEntreesForFifo(@Param("produitId") Long produitId);

    // Calculate total quantity for a product
    @Query("SELECT COALESCE(SUM(CASE WHEN m.typeMouvement = 'ENTREE' THEN m.quantite WHEN m.typeMouvement = 'SORTIE' THEN -m.quantite ELSE 0 END), 0) FROM MouvementStock m WHERE m.produit.id = :produitId")
    Integer calculateStockActuel(@Param("produitId") Long produitId);
}