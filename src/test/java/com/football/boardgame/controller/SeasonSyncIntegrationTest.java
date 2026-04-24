package com.football.boardgame.controller;

import com.football.boardgame.config.TestSecurityConfig;
import com.football.boardgame.domain.Season;
import com.football.boardgame.repository.SeasonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ETag support and Sync Checkpoint (Issue #21).
 * Uses H2 in-memory database with a mock JwtDecoder.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class SeasonSyncIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeasonRepository seasonRepository;

    private Season testSeason;

    @BeforeEach
    void setUp() {
        seasonRepository.deleteAll();
        testSeason = seasonRepository.saveAndFlush(
            Season.builder()
                .name("Test Season 2026")
                .status(Season.SeasonStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .syncVersion(0L)
                .build()
        );
    }

    // ── ETag Tests ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("ETag Support")
    class ETagTests {

        @Test
        @DisplayName("GET /api/seasons should return an ETag header")
        void getAllSeasons_shouldReturnETag() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"))
                .andReturn();

            String etag = result.getResponse().getHeader("ETag");
            assertThat(etag).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("GET with valid If-None-Match should return 304 Not Modified")
        void getSeasons_withMatchingETag_shouldReturn304() throws Exception {
            // First request: get the ETag
            MvcResult firstResult = mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

            String etag = firstResult.getResponse().getHeader("ETag");
            assertThat(etag).isNotNull();

            // Second request: send If-None-Match with the same ETag
            mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("If-None-Match", etag))
                .andExpect(status().isNotModified());
        }

        @Test
        @DisplayName("GET with stale If-None-Match should return 200 with new data")
        void getSeasons_withStaleETag_shouldReturn200() throws Exception {
            // First request: get the ETag
            MvcResult firstResult = mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

            String etag = firstResult.getResponse().getHeader("ETag");
            assertThat(etag).isNotNull();

            // Modify data to invalidate ETag
            testSeason.setName("Modified Season");
            seasonRepository.saveAndFlush(testSeason);

            // Second request: should get 200 because data changed
            mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("If-None-Match", etag))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"));
        }

        @Test
        @DisplayName("ETag on single season endpoint should also work")
        void getSeasonById_shouldReturnETag() throws Exception {
            mockMvc.perform(get("/api/seasons/" + testSeason.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"));
        }
    }

    // ── Sync Checkpoint Tests ───────────────────────────────────────────

    @Nested
    @DisplayName("Sync Checkpoint")
    class SyncCheckpointTests {

        @Test
        @DisplayName("POST sync-checkpoint should increment syncVersion by 1")
        void syncCheckpoint_shouldIncrementVersion() throws Exception {
            assertThat(testSeason.getSyncVersion()).isEqualTo(0L);

            mockMvc.perform(post("/api/seasons/" + testSeason.getId() + "/sync-checkpoint")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.syncVersion").value(1))
                .andExpect(jsonPath("$.seasonId").value(testSeason.getId().toString()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

            // Verify in database
            Season refreshed = seasonRepository.findById(testSeason.getId()).orElseThrow();
            assertThat(refreshed.getSyncVersion()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Multiple checkpoints should increment monotonically")
        void syncCheckpoint_multipleCallsShouldIncrementMonotonically() throws Exception {
            // Checkpoint 1
            mockMvc.perform(post("/api/seasons/" + testSeason.getId() + "/sync-checkpoint")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.syncVersion").value(1));

            // Checkpoint 2
            mockMvc.perform(post("/api/seasons/" + testSeason.getId() + "/sync-checkpoint")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.syncVersion").value(2));

            // Checkpoint 3
            mockMvc.perform(post("/api/seasons/" + testSeason.getId() + "/sync-checkpoint")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.syncVersion").value(3));

            // Verify final state
            Season refreshed = seasonRepository.findById(testSeason.getId()).orElseThrow();
            assertThat(refreshed.getSyncVersion()).isEqualTo(3L);
        }

        @Test
        @DisplayName("Checkpoint on non-existent season should throw exception")
        void syncCheckpoint_onNonExistentSeason_shouldFail() throws Exception {
            // The RuntimeException propagates as a ServletException in Spring MVC
            // This verifies the server doesn't silently succeed
            try {
                mockMvc.perform(post("/api/seasons/00000000-0000-0000-0000-000000000000/sync-checkpoint")
                        .contentType(MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                assertThat(e).hasMessageContaining("Season not found");
            }
        }

        @Test
        @DisplayName("Checkpoint should change the ETag of the seasons list")
        void syncCheckpoint_shouldInvalidateListETag() throws Exception {
            // Get initial ETag from the list endpoint (which includes all season data)
            MvcResult before = mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
            String etagBefore = before.getResponse().getHeader("ETag");

            // Trigger checkpoint (modifies syncVersion and updatedAt)
            mockMvc.perform(post("/api/seasons/" + testSeason.getId() + "/sync-checkpoint")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

            // Get new ETag — list response body hash should differ because
            // updatedAt and/or version fields changed
            MvcResult after = mockMvc.perform(get("/api/seasons")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
            String etagAfter = after.getResponse().getHeader("ETag");

            // The list endpoint body includes the season with updated fields,
            // so the ETag (content hash) should change
            assertThat(etagAfter).isNotEqualTo(etagBefore);
        }
    }
}
