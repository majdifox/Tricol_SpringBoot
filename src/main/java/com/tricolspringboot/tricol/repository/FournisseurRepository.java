package com.tricolspringboot.tricol.repository;

import com.tricolspringboot.tricol.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    // Custom query methods
    Optional<Fournisseur> findByIce(String ice);

    boolean existsByIce(String ice);
}