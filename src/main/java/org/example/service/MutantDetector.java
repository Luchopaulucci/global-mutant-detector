package org.example.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service for detecting mutants based on DNA sequence analysis.
 * 
 * A human is considered a mutant if their DNA contains more than one sequence
 * of four identical letters (A, T, C, G) in any direction: horizontal,
 * vertical,
 * or diagonal.
 * 
 * Optimizations implemented:
 * - Early Termination: stops as soon as >1 sequences are found
 * - Single Pass: traverses the matrix only once
 * - Boundary Checking: validates boundaries before searching
 * - Direct Comparison: uses direct comparison instead of loops
 * - Validation Set O(1): constant-time character validation
 */
@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Determines if a DNA sequence belongs to a mutant.
     * 
     * @param dna Array of strings representing DNA sequences (NxN matrix)
     * @return true if mutant (>1 sequences found), false otherwise
     */
    public boolean isMutant(String[] dna) {
        // Validation: null or empty array
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;

        // Validation: minimum size
        if (n < SEQUENCE_LENGTH) {
            return false;
        }

        // Convert to char[][] for O(1) access and validate
        char[][] matrix = new char[n][];
        for (int i = 0; i < n; i++) {
            // Validation: null row
            if (dna[i] == null) {
                return false;
            }

            // Validation: non-square matrix
            if (dna[i].length() != n) {
                return false;
            }

            matrix[i] = dna[i].toCharArray();

            // Validation: only A, T, C, G allowed
            for (char c : matrix[i]) {
                if (!VALID_BASES.contains(c)) {
                    return false;
                }
            }
        }

        int sequenceCount = 0;

        // Single Pass: traverse matrix once checking all directions
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                char base = matrix[row][col];

                // Boundary Checking: only check if sequence can fit

                // Check Horizontal (→)
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkHorizontal(matrix, row, col, base)) {
                        sequenceCount++;
                        if (sequenceCount > 1)
                            return true; // Early Termination
                    }
                }

                // Check Vertical (↓)
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkVertical(matrix, row, col, base)) {
                        sequenceCount++;
                        if (sequenceCount > 1)
                            return true; // Early Termination
                    }
                }

                // Check Diagonal Descending (↘)
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalDescending(matrix, row, col, base)) {
                        sequenceCount++;
                        if (sequenceCount > 1)
                            return true; // Early Termination
                    }
                }

                // Check Diagonal Ascending (↗)
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalAscending(matrix, row, col, base)) {
                        sequenceCount++;
                        if (sequenceCount > 1)
                            return true; // Early Termination
                    }
                }
            }
        }

        return false;
    }

    /**
     * Direct Comparison: checks horizontal sequence without loop.
     * More efficient than generic checkDirection with loop.
     */
    private boolean checkHorizontal(char[][] matrix, int row, int col, char base) {
        return matrix[row][col + 1] == base &&
                matrix[row][col + 2] == base &&
                matrix[row][col + 3] == base;
    }

    /**
     * Direct Comparison: checks vertical sequence without loop.
     */
    private boolean checkVertical(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col] == base &&
                matrix[row + 2][col] == base &&
                matrix[row + 3][col] == base;
    }

    /**
     * Direct Comparison: checks diagonal descending sequence without loop.
     */
    private boolean checkDiagonalDescending(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col + 1] == base &&
                matrix[row + 2][col + 2] == base &&
                matrix[row + 3][col + 3] == base;
    }

    /**
     * Direct Comparison: checks diagonal ascending sequence without loop.
     */
    private boolean checkDiagonalAscending(char[][] matrix, int row, int col, char base) {
        return matrix[row - 1][col + 1] == base &&
                matrix[row - 2][col + 2] == base &&
                matrix[row - 3][col + 3] == base;
    }
}
