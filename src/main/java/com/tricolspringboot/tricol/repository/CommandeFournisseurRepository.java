package com.tricolspringboot.tricol.repository;

import com.tricolspringboot.tricol.entity.CommandeFournisseur;
import com.tricolspringboot.tricol.enums.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {

    // Find by status with pagination
    Page<CommandeFournisseur> findByStatut(StatutCommande statut, Pageable pageable);

    // Find by fournisseur ID
    List<CommandeFournisseur> findByFournisseurId(Long fournisseurId);

    // Find by date range
    List<CommandeFournisseur> findByDateCommandeBetween(LocalDate startDate, LocalDate endDate);

    // Custom JPQL query - find commandes by fournisseur and status
    @Query("SELECT c FROM CommandeFournisseur c WHERE c.fournisseur.id = :fournisseurId AND c.statut = :statut")
    List<CommandeFournisseur> findByFournisseurAndStatut(
            @Param("fournisseurId") Long fournisseurId,
            @Param("statut") StatutCommande statut
    );

    // Count commandes by status
    long countByStatut(StatutCommande statut);
}