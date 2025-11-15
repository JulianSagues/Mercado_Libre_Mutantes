package org.example.mercadolibre.service;

import org.example.mercadolibre.entity.Dna;
import org.example.mercadolibre.repository.DnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class MutantService {

    @Autowired
    private DnaRepository dnaRepository;

    /**
     * Verifica si un ADN pertenece a un mutante
     * @param dna Array de Strings que representa cada fila de una tabla de NxN
     * @return true si es mutante, false si no lo es
     */
    public boolean isMutant(String[] dna) {
        // Validaciones básicas
        if (dna == null || dna.length < 4) {
            return false;
        }

        int n = dna.length;

        // Validar que sea una matriz cuadrada y contenga solo caracteres válidos
        for (String row : dna) {
            if (row == null || row.length() != n || !row.matches("[ATCG]+")) {
                throw new IllegalArgumentException("ADN inválido: debe ser una matriz NxN con solo caracteres A, T, C, G");
            }
        }

        int sequencesFound = 0;

        // 1. Buscar secuencias horizontales
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= n - 4; j++) {
                if (hasSequence(dna[i].substring(j, j + 4))) {
                    sequencesFound++;
                    if (sequencesFound > 1) return true;
                }
            }
        }

        // 2. Buscar secuencias verticales
        for (int j = 0; j < n; j++) {
            for (int i = 0; i <= n - 4; i++) {
                StringBuilder vertical = new StringBuilder();
                for (int k = 0; k < 4; k++) {
                    vertical.append(dna[i + k].charAt(j));
                }
                if (hasSequence(vertical.toString())) {
                    sequencesFound++;
                    if (sequencesFound > 1) return true;
                }
            }
        }

        // 3. Buscar secuencias diagonales (descendentes ↘)
        for (int i = 0; i <= n - 4; i++) {
            for (int j = 0; j <= n - 4; j++) {
                StringBuilder diagonal = new StringBuilder();
                for (int k = 0; k < 4; k++) {
                    diagonal.append(dna[i + k].charAt(j + k));
                }
                if (hasSequence(diagonal.toString())) {
                    sequencesFound++;
                    if (sequencesFound > 1) return true;
                }
            }
        }

        // 4. Buscar secuencias diagonales (ascendentes ↗)
        for (int i = 3; i < n; i++) {
            for (int j = 0; j <= n - 4; j++) {
                StringBuilder diagonal = new StringBuilder();
                for (int k = 0; k < 4; k++) {
                    diagonal.append(dna[i - k].charAt(j + k));
                }
                if (hasSequence(diagonal.toString())) {
                    sequencesFound++;
                    if (sequencesFound > 1) return true;
                }
            }
        }

        return false;
    }

    /**
     * Verifica si una secuencia de 4 caracteres son todos iguales
     */
    private boolean hasSequence(String sequence) {
        if (sequence.length() != 4) return false;
        char first = sequence.charAt(0);
        return sequence.equals(String.valueOf(first).repeat(4));
    }

    /**
     * Analiza el ADN y lo guarda en la base de datos
     * @param dna Array de strings con el ADN
     * @return true si es mutante, false si no
     */
    public boolean analyzeDna(String[] dna) {
        // Generar hash único del ADN
        String dnaHash = generateHash(dna);

        // Verificar si ya existe en la base de datos
        Optional<Dna> existingDna = dnaRepository.findByDnaHash(dnaHash);
        if (existingDna.isPresent()) {
            return existingDna.get().isMutant();
        }

        // Analizar el ADN
        boolean isMutant = isMutant(dna);

        // Guardar en la base de datos
        Dna dnaEntity = new Dna(dnaHash, isMutant, Arrays.toString(dna));
        dnaRepository.save(dnaEntity);

        return isMutant;
    }

    /**
     * Obtiene las estadísticas de verificaciones de ADN
     */
    public org.example.mercadolibre.dto.StatsResponse getStats() {
        long countMutant = dnaRepository.countByIsMutant(true);
        long countHuman = dnaRepository.countByIsMutant(false);

        return new org.example.mercadolibre.dto.StatsResponse(countMutant, countHuman);
    }

    /**
     * Genera un hash SHA-256 del ADN para identificarlo de forma única
     */
    private String generateHash(String[] dna) {
        try {
            String dnaString = String.join("", dna);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dnaString.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash del ADN", e);
        }
    }
}


