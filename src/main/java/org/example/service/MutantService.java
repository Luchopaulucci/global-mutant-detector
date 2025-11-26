package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.DnaRecord;
import org.example.exception.DnaHashCalculationException;
import org.example.repository.DnaRecordRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Servicio para analizar secuencias de ADN y gestionar resultados de detección de mutantes.
 */
@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    @Cacheable(value = "dnaCache", key = "#dna")
    public boolean analyzeDna(String[] dna) {
        String dnaHash = calculateDnaHash(dna);

        // Mira si esta duplicado
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant();
        }

        // Analiza el DNA
        boolean isMutant = mutantDetector.isMutant(dna);

        // guarda el resultado
        DnaRecord record = new DnaRecord();
        record.setDnaHash(dnaHash);
        record.setMutant(isMutant);
        dnaRecordRepository.save(record);

        return isMutant;
    }

    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String combinedDna = Arrays.toString(dna);
            byte[] hash = digest.digest(combinedDna.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DnaHashCalculationException("Error calculating DNA hash", e);
        }
    }
}
