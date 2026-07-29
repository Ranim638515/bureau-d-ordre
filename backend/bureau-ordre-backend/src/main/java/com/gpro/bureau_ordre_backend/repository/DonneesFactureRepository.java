package com.gpro.bureau_ordre_backend.repository;

import com.gpro.bureau_ordre_backend.model.DonneesFacture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonneesFactureRepository extends JpaRepository<DonneesFacture, Long> {
}