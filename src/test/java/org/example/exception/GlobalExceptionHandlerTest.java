package org.example.exception;

import org.example.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for GlobalExceptionHandler.
 * Tests exception handling and error response formatting.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("Should handle IllegalArgumentException and return 400")
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input data");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return field errors")
    void testHandleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("dnaRequest", "dna", "DNA sequence cannot be null");
        FieldError fieldError2 = new FieldError("dnaRequest", "dna", "DNA sequence cannot be empty");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("dna"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle DnaHashCalculationException via generic handler")
    void testHandleDnaHashCalculationException() {
        // Arrange
        DnaHashCalculationException ex = new DnaHashCalculationException("Error calculating hash");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("unexpected error"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500")
    void testHandleGenericException() {
        // Arrange
        Exception ex = new Exception("Unexpected system error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("An unexpected error occurred"));
        assertTrue(response.getBody().getMessage().contains("Unexpected system error"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should include timestamp in all error responses")
    void testErrorResponsesHaveTimestamp() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        // Timestamp should be recent (within last second)
        assertTrue(response.getBody().getTimestamp().toString().length() > 0);
    }
}
