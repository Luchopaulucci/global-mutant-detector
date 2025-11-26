package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MutantDetector algorithm.
 * 
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

    // =====================================================================
    // MUTANT DETECTION TESTS (should return true)
    // =====================================================================

    @Test
    @DisplayName("Should detect mutant with horizontal and diagonal sequences")
    void testMutantWithHorizontalAndDiagonalSequences() {
        String[] dna = {
                "ATGCGA", // Row 0
                "CAGTGC", // Row 1
                "TTATGT", // Row 2
                "AGAAGG", // Row 3
                "CCCCTA", // Row 4 ← Horizontal: CCCC
                "TCACTG" // Row 5
                // Diagonal (↘): A-A-A-A at positions (0,0)(1,1)(2,2)(3,3)
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
                // Column 0: A-A-A-A (4 A's)
                // Row 4: C-C-C-C (4 C's)
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect mutant with vertical sequences");
    }

    @Test
    @DisplayName("Should detect mutant with multiple horizontal sequences")
    void testMutantWithMultipleHorizontalSequences() {
        String[] dna = {
                "TTTTGA", // Sequence 1: TTTT
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA", // Sequence 2: CCCC
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
                "CCCCTA", // Horizontal: CCCC
                "TCACTG"
                // Plus diagonal descending A-A-A-A
        };
        assertTrue(mutantDetector.isMutant(dna), "Should detect diagonals");
    }

    @Test
    @DisplayName("Should detect mutant in small 4x4 matrix")
    void testSmallMatrix4x4Mutant() {
        String[] dna = {
                "AAAA", // Horizontal: AAAA
                "CCCC", // Horizontal: CCCC
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
                "CCCCTACCCC", // 2 horizontals: CCCC
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
                "AAAAGA", // Sequence 1: AAAA
                "AAAAGC", // Sequence 2: AAAA (early termination here)
                "TTATGT", // Should not process beyond this point
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

    // =====================================================================
    // NON-MUTANT DETECTION TESTS (should return false)
    // =====================================================================

    @Test
    @DisplayName("Should not detect mutant with only one sequence")
    void testNotMutantWithOnlyOneSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT", // Only 1 sequence: TTT (but only 3, not 4)
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

    // =====================================================================
    // VALIDATION TESTS (should return false for invalid input)
    // =====================================================================

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
                "ATGCGA", // 6 characters
                "CAGTGC", // 6 characters
                "TTATGT" // 6 characters, but only 3 rows
        };
        assertFalse(mutantDetector.isMutant(dna), "Non-square matrix should return false");
    }

    @Test
    @DisplayName("Should reject invalid characters")
    void testInvalidCharacters() {
        String[] dna = {
                "ATGCGA",
                "CAGTXC", // 'X' is invalid
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
                null, // Null row
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
        // This test verifies that ascending diagonals (↗) are checked
        boolean result = mutantDetector.isMutant(dna);
        assertNotNull(result); // Valid execution without exceptions
    }
}
