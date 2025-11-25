package org.example.mercadolibre.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adn_datos")
public class Dna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adn_hash", nullable = false, unique = true, length = 64)
    private String dnaHash;

    @Column(name = "es_mutante", nullable = false)
    private boolean isMutant;

    @Column(name = "creacion", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "secuencia_adn", columnDefinition = "TEXT")
    private String dnaSequence;

    public Dna() {
        this.createdAt = LocalDateTime.now();
    }

    public Dna(String dnaHash, boolean isMutant, String dnaSequence) {
        this.dnaHash = dnaHash;
        this.isMutant = isMutant;
        this.dnaSequence = dnaSequence;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDnaHash() {
        return dnaHash;
    }

    public void setDnaHash(String dnaHash) {
        this.dnaHash = dnaHash;
    }

    public boolean isMutant() {
        return isMutant;
    }

    public void setMutant(boolean mutant) {
        isMutant = mutant;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDnaSequence() {
        return dnaSequence;
    }

    public void setDnaSequence(String dnaSequence) {
        this.dnaSequence = dnaSequence;
    }
}
