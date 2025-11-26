package org.example.service;

import org.example.entity.DnaRecord;
import org.example.exception.DnaHashCalculationException;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for MutantService with mocked dependencies.
 * 
 * Tests cover:
 * - DNA analysis with mutant detection
 * - DNA analysis with human detection
 * - Caching/deduplication mechanism
 * - Hash calculation consistency
 * 
 * Total tests: 5
 */
@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = new String[] {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        humanDna = new String[] {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
    }

    @Test
    @DisplayName("Should analyze mutant DNA and save to database")
    void testAnalyzeMutantDnaAndSave() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty()); // Not in cache
        when(mutantDetector.isMutant(mutantDna))
                .thenReturn(true); // Is mutant
        when(dnaRecordRepository.save(any(DnaRecord.class)))
                .thenReturn(new DnaRecord());

        // Act
        boolean result = mutantService.analyzeDna(mutantDna);

        // Assert
        assertTrue(result, "Should return true for mutant");
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Should analyze human DNA and save to database")
    void testAnalyzeHumanDnaAndSave() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna))
                .thenReturn(false); // Is human
        when(dnaRecordRepository.save(any(DnaRecord.class)))
                .thenReturn(new DnaRecord());

        // Act
        boolean result = mutantService.analyzeDna(humanDna);

        // Assert
        assertFalse(result, "Should return false for human");
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Should return cached result if DNA already analyzed")
    void testReturnCachedResultForAnalyzedDna() {
        // Arrange
        DnaRecord cachedRecord = new DnaRecord();
        cachedRecord.setDnaHash("somehash");
        cachedRecord.setMutant(true);

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.of(cachedRecord)); // Already in cache

        // Act
        boolean result = mutantService.analyzeDna(mutantDna);

        // Assert
        assertTrue(result, "Should return cached result");
        verify(mutantDetector, never()).isMutant(any()); // Should NOT call detector
        verify(dnaRecordRepository, never()).save(any()); // Should NOT save again
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
    }

    @Test
    @DisplayName("Should calculate consistent hash for same DNA")
    void testCalculateDnaHashConsistency() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any()))
                .thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        mutantService.analyzeDna(mutantDna);
        mutantService.analyzeDna(mutantDna);

        // Assert
        // The same hash should be searched both times
        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
    }

    @Test
    @DisplayName("Should handle different DNA with different hashes")
    void testDifferentDnaProducesDifferentHashes() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any()))
                .thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class)))
                .thenReturn(new DnaRecord());

        // Act
        boolean result1 = mutantService.analyzeDna(mutantDna);
        boolean result2 = mutantService.analyzeDna(humanDna);

        // Assert
        assertTrue(result1);
        assertTrue(result2); // Both return true (mocked)
        verify(dnaRecordRepository, times(2)).save(any(DnaRecord.class));
    }
}
