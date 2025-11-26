package org.example.service;

import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for StatsService with mocked repository.
 * 
 * Tests cover:
 * - Statistics with no records
 * - Statistics with only mutants
 * - Statistics with only humans
 * - Statistics with mixed records
 * - Ratio calculation edge cases
 * 
 * Total tests: 6
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Should return zeros when no records exist")
    void testGetStatsWhenNoRecords() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(0, stats.getCountMutantDna(), "Mutant count should be 0");
        assertEquals(0, stats.getCountHumanDna(), "Human count should be 0");
        assertEquals(0.0, stats.getRatio(), 0.001, "Ratio should be 0 when no humans");
    }

    @Test
    @DisplayName("Should calculate stats with only mutants")
    void testGetStatsWithOnlyMutants() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(40, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001, "Ratio should be 0 when no humans");
    }

    @Test
    @DisplayName("Should calculate stats with only humans")
    void testGetStatsWithOnlyHumans() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(0, stats.getCountMutantDna());
        assertEquals(100, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001, "Ratio should be 0 when no mutants");
    }

    @Test
    @DisplayName("Should calculate stats with mixed records")
    void testGetStatsWithMixedRecords() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(40, stats.getCountMutantDna());
        assertEquals(100, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 0.001, "Ratio should be 40/100 = 0.4");
    }

    @Test
    @DisplayName("Should calculate ratio correctly")
    void testGetStatsRatioCalculation() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(25L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(50, stats.getCountMutantDna());
        assertEquals(25, stats.getCountHumanDna());
        assertEquals(2.0, stats.getRatio(), 0.001, "Ratio should be 50/25 = 2.0");
    }

    @Test
    @DisplayName("Should handle division by zero for ratio calculation")
    void testGetStatsWithZeroHumans() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(10, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001, "Ratio should be 0 to avoid division by zero");
        verify(dnaRecordRepository, times(1)).countByIsMutant(true);
        verify(dnaRecordRepository, times(1)).countByIsMutant(false);
    }
}
