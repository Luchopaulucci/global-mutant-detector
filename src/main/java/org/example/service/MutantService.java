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
 * Service for analyzing DNA sequences and managing mutant detection results.
 * 
 * Handles caching of DNA analysis results using SHA-256 hash for deduplication.
 */
@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Analyzes a DNA sequence to determine if it belongs to a mutant.
     * Results are cached in memory (L1) and database (L2) to avoid redundant
     * processing.
     * 
     * @param dna Array of strings representing the DNA sequence
     * @return true if mutant, false if human
     */
    @Cacheable(value = "dnaCache", key = "#dna")
    public boolean analyzeDna(String[] dna) {
        String dnaHash = calculateDnaHash(dna);

        // Check if already analyzed (deduplication)
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant();
        }

        // Analyze DNA
        boolean isMutant = mutantDetector.isMutant(dna);

        // Save result for future queries
        DnaRecord record = new DnaRecord();
        record.setDnaHash(dnaHash);
        record.setMutant(isMutant);
        dnaRecordRepository.save(record);

        return isMutant;
    }

    /**
     * Calculates SHA-256 hash of DNA sequence for deduplication.
     * 
     * @param dna Array of strings representing the DNA sequence
     * @return Hexadecimal string representation of the SHA-256 hash
     * @throws DnaHashCalculationException if hash calculation fails
     */
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
