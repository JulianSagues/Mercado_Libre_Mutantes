package org.example.mercadolibre.repository;

import org.example.mercadolibre.entity.Dna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DnaRepository extends JpaRepository<Dna, Long> {

    // Buscar por hash para evitar duplicados
    Optional<Dna> findByDnaHash(String dnaHash);

    // Contar mutantes
    long countByIsMutant(boolean isMutant);

    // Verificar si existe un ADN por su hash
    boolean existsByDnaHash(String dnaHash);
}

