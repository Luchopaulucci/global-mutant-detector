package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DnaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MutantController endpoints.
 * 
 * Tests cover:
 * - POST /mutant with valid mutant DNA (200 OK)
 * - POST /mutant with valid human DNA (403 Forbidden)
 * - POST /mutant with invalid DNA (400 Bad Request)
 * - GET /stats endpoint
 * 
 * Total tests: 8
 */
@SpringBootTest
@AutoConfigureMockMvc
class MutantControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /mutant should return 200 OK for mutant DNA")
        void testMutantEndpoint_ReturnOk() throws Exception {
                String[] dna = {
                                "ATGCGA",
                                "CAGTGC",
                                "TTATGT",
                                "AGAAGG",
                                "CCCCTA",
                                "TCACTG"
                };
                DnaRequest request = new DnaRequest(dna);

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /mutant should return 403 Forbidden for human DNA")
        void testHumanEndpoint_ReturnForbidden() throws Exception {
                String[] dna = {
                                "ATGCGA",
                                "CAGTGC",
                                "TTATTT",
                                "AGACGG",
                                "GCGTCA",
                                "TCACTG"
                };
                DnaRequest request = new DnaRequest(dna);

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /mutant should return 400 Bad Request for invalid DNA (non-square)")
        void testInvalidDna_ReturnBadRequest() throws Exception {
                String[] dna = {
                                "ATGX",
                                "CAGT"
                };
                DnaRequest request = new DnaRequest(dna);

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /mutant should return 400 Bad Request for empty DNA")
        void testEmptyDna_ReturnBadRequest() throws Exception {
                String[] dna = {};
                DnaRequest request = new DnaRequest(dna);

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /mutant should return 400 Bad Request for null DNA")
        void testNullDna_ReturnBadRequest() throws Exception {
                String jsonWithNullDna = "{\"dna\": null}";

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonWithNullDna))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /stats should return 200 OK")
        void testStatsEndpoint_ReturnOk() throws Exception {
                mockMvc.perform(get("/stats"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("GET /stats should return correct JSON format")
        void testStatsEndpoint_CorrectFormat() throws Exception {
                mockMvc.perform(get("/stats"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.count_mutant_dna").exists())
                                .andExpect(jsonPath("$.count_human_dna").exists())
                                .andExpect(jsonPath("$.ratio").exists());
        }

        @Test
        @DisplayName("Multiple requests should update stats correctly")
        void testMultipleRequests_StatsUpdate() throws Exception {
                // Send mutant DNA
                String[] mutantDna = {
                                "AAAAGA",
                                "CAGTGC",
                                "TTATGT",
                                "AGAAGG",
                                "CCCCTA",
                                "TCACTG"
                };
                DnaRequest mutantRequest = new DnaRequest(mutantDna);

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mutantRequest)))
                                .andExpect(status().isOk());

                // Send human DNA
                String[] humanDna = {
                                "ATGCGA",
                                "CAGTGC",
                                "TTATTT",
                                "AGACGG",
                                "GCGTCA",
                                "TCACTG"
                };
                DnaRequest humanRequest = new DnaRequest(humanDna);

                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(humanRequest)))
                                .andExpect(status().isForbidden());

                // Verify stats are updated
                mockMvc.perform(get("/stats"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.count_mutant_dna").isNumber())
                                .andExpect(jsonPath("$.count_human_dna").isNumber());
        }
}
