package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Servicio de detección de mutantes basado en análisis de secuencias de ADN.
 */
@Slf4j
@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    public boolean isMutant(String[] dna) {
        log.debug("Starting mutant detection analysis");

        // Validation: array nulo o vacio
        if (dna == null || dna.length == 0) {
            log.warn("DNA validation failed: null or empty array");
            return false;
        }

        int n = dna.length;
        log.debug("Analyzing DNA matrix of size {}x{}", n, n);

        // Validation: tamaño minimo
        if (n < SEQUENCE_LENGTH) {
            log.warn("DNA validation failed: matrix size {}x{} is below minimum {}", n, n, SEQUENCE_LENGTH);
            return false;
        }

        // Convertir a char[][] para acceder a O(1) y validar
        char[][] matrix = new char[n][];
        for (int i = 0; i < n; i++) {
            // Validation: fila nula
            if (dna[i] == null) {
                log.warn("DNA validation failed: null row at index {}", i);
                return false;
            }

            // Validation: matriz no cuadrada
            if (dna[i].length() != n) {
                log.warn("DNA validation failed: row {} has length {} (expected {})", i, dna[i].length(), n);
                return false;
            }

            matrix[i] = dna[i].toCharArray();

            // Validation: solo A, T, C, G permitidas
            for (char c : matrix[i]) {
                if (!VALID_BASES.contains(c)) {
                    log.warn("DNA validation failed: invalid character '{}' found at row {}", c, i);
                    return false;
                }
            }
        }

        int sequenceCount = 0;

        // Paso único: recorra la matriz una vez verificando todas las direcciones
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                char base = matrix[row][col];

                // Check Horizontal (→)
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkHorizontal(matrix, row, col, base)) {
                        sequenceCount++;
                        log.debug("Horizontal sequence found at row={}, col={}, base='{}', count={}", row, col, base,
                                sequenceCount);
                        if (sequenceCount > 1) {
                            log.info("Mutant detected! Found {} sequences. Early termination.", sequenceCount);
                            return true; // Early Termination
                        }
                    }
                }

                // Check Vertical (↓)
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkVertical(matrix, row, col, base)) {
                        sequenceCount++;
                        log.debug("Vertical sequence found at row={}, col={}, base='{}', count={}", row, col, base,
                                sequenceCount);
                        if (sequenceCount > 1) {
                            log.info("Mutant detected! Found {} sequences. Early termination.", sequenceCount);
                            return true; // Early Termination
                        }
                    }
                }

                // Check Diagonal Descending (↘)
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalDescending(matrix, row, col, base)) {
                        sequenceCount++;
                        log.debug("Diagonal descending sequence found at row={}, col={}, base='{}', count={}", row, col,
                                base, sequenceCount);
                        if (sequenceCount > 1) {
                            log.info("Mutant detected! Found {} sequences. Early termination.", sequenceCount);
                            return true; // Early Termination
                        }
                    }
                }

                // Check Diagonal Ascending (↗)
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalAscending(matrix, row, col, base)) {
                        sequenceCount++;
                        log.debug("Diagonal ascending sequence found at row={}, col={}, base='{}', count={}", row, col,
                                base, sequenceCount);
                        if (sequenceCount > 1) {
                            log.info("Mutant detected! Found {} sequences. Early termination.", sequenceCount);
                            return true; // Early Termination
                        }
                    }
                }
            }
        }

        log.info("Analysis complete. Result: {} (sequences found: {})", sequenceCount > 1 ? "MUTANT" : "HUMAN",
                sequenceCount);
        return false;
    }

    /**
     * Comparación directa: comprueba secuencia horizontal sin bucle.
     * Más eficiente que checkDirection genérico con bucle.
     */
    private boolean checkHorizontal(char[][] matrix, int row, int col, char base) {
        return matrix[row][col + 1] == base &&
                matrix[row][col + 2] == base &&
                matrix[row][col + 3] == base;
    }

    /**
     * Comparación directa: comprueba secuencia vertical sin bucle.
     */
    private boolean checkVertical(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col] == base &&
                matrix[row + 2][col] == base &&
                matrix[row + 3][col] == base;
    }

    /**
     * Comparación directa: comprueba la secuencia descendente diagonal sin bucle.
     */
    private boolean checkDiagonalDescending(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col + 1] == base &&
                matrix[row + 2][col + 2] == base &&
                matrix[row + 3][col + 3] == base;
    }

    /**
     * Comparación directa: comprueba la secuencia ascendente diagonal sin bucle.
     */
    private boolean checkDiagonalAscending(char[][] matrix, int row, int col, char base) {
        return matrix[row - 1][col + 1] == base &&
                matrix[row - 2][col + 2] == base &&
                matrix[row - 3][col + 3] == base;
    }
}
