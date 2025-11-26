package org.example.exception;

/**
 * Se lanza una excepción personalizada cuando falla el cálculo del hash de ADN.
 */
public class DnaHashCalculationException extends RuntimeException {

    public DnaHashCalculationException(String message) {
        super(message);
    }

    public DnaHashCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
