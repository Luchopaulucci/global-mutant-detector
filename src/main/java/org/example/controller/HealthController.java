package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller for health check endpoint.
 * Provides application health status without depending on business logic.
 */
@RestController
@RequestMapping
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @Operation(summary = "Health check", description = "Returns the application health status and current timestamp")
    @ApiResponse(responseCode = "200", description = "Application is healthy", content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()));
    }
}
