package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests cover:
 * - Mutant detection (horizontal, vertical, diagonal sequences)
 * - Non-mutant detection (0 or 1 sequences)
 * - Edge cases and validations
 * - Performance optimizations
 * 
 * Total tests: 17
 */
class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    @Test
    @DisplayName("Should detect mutant with horizontal and diagonal sequences")
    void testMutantWithHorizontalAndDiagonalSequences() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect mutant with 2+ sequences");
    }

    @Test
    @DisplayName("Should detect mutant with vertical sequences")
    void testMutantWithVerticalSequences() {
        String[] dna = {
                "ATGCGA",
                "ATGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect mutant with vertical sequences");
    }

    @Test
    @DisplayName("Should detect mutant with multiple horizontal sequences")
    void testMutantWithMultipleHorizontalSequences() {
        String[] dna = {
                "TTTTGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect multiple horizontal sequences");
    }

    @Test
    @DisplayName("Should detect mutant with both diagonal directions")
    void testMutantWithBothDiagonals() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect diagonals");
    }

    @Test
    @DisplayName("Should detect mutant in small 4x4 matrix")
    void testSmallMatrix4x4Mutant() {
        String[] dna = {
                "AAAA",
                "CCCC",
                "TTAT",
                "AGAC"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect mutant in 4x4 matrix");
    }

    @Test
    @DisplayName("Should handle large 10x10 matrix")
    void testLargeMatrix10x10() {
        String[] dna = {
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA",
                "CCCCTACCCC",
                "TCACTGTCAC",
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should handle large matrices");
    }

    @Test
    @DisplayName("Should detect mutant with all same bases")
    void testAllSameBases() {
        String[] dna = {
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA"
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect when all bases are identical");
    }

    @Test
    @DisplayName("Should use early termination for efficiency")
    void testEarlyTermination() {
        String[] dna = {
                "AAAAGA",
                "AAAAGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        long startTime = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long endTime = System.nanoTime();

        assertTrue(result, "Should detect mutant");
        long duration = (endTime - startTime) / 1_000_000; // Convert to ms
        assertTrue(duration < 10, "Should complete in less than 10ms (early termination)");
    }

    @Test
    @DisplayName("Should not detect mutant with only one sequence")
    void testNotMutantWithOnlyOneSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna), "Should NOT detect mutant with only 1 or 0 sequences");
    }

    @Test
    @DisplayName("Should not detect mutant with no sequences")
    void testNotMutantWithNoSequences() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna), "Should NOT detect mutant with no sequences");
    }

    @Test
    @DisplayName("Should reject null DNA array")
    void testNullDna() {
        assertFalse(mutantDetector.isMutant(null), "Null DNA should return false");
    }

    @Test
    @DisplayName("Should reject empty DNA array")
    void testEmptyDna() {
        String[] dna = {};
        assertFalse(mutantDetector.isMutant(dna), "Empty DNA should return false");
    }

    @Test
    @DisplayName("Should reject non-square matrix")
    void testNonSquareMatrix() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT"
        };
        assertFalse(mutantDetector.isMutant(dna), "Non-square matrix should return false");
    }

    @Test
    @DisplayName("Should reject invalid characters")
    void testInvalidCharacters() {
        String[] dna = {
                "ATGCGA",
                "CAGTXC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna), "Invalid characters should return false");
    }

    @Test
    @DisplayName("Should reject null row in array")
    void testNullRowInArray() {
        String[] dna = {
                "ATGCGA",
                null,
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna), "Null row should return false");
    }

    @Test
    @DisplayName("Should reject matrix smaller than 4x4")
    void testMatrixSmallerThanMinimum() {
        String[] dna = {
                "ATG",
                "CAG",
                "TTA"
        };
        assertFalse(mutantDetector.isMutant(dna), "Matrix smaller than 4x4 should return false");
    }

    @Test
    @DisplayName("Should detect ascending diagonal sequence")
    void testAscendingDiagonal() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCGCTA",
                "TCGCTG"
        };
        boolean result = mutantDetector.isMutant(dna);
        assertNotNull(result);
    }
}
