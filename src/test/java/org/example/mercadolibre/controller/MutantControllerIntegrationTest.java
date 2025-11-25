package org.example.mercadolibre.controller;

import org.example.mercadolibre.dto.StatsResponse;
import org.example.mercadolibre.repository.DnaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MutantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DnaRepository dnaRepository;

    @BeforeEach
    void setUp() {
        dnaRepository.deleteAll();
    }

    @Test
    void testPostMutant_ShouldReturn200_WhenIsMutant() throws Exception {
        String mutantDna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutantDna))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"message\": \"Es un mutante\"}"));
    }

    @Test
    void testPostMutant_ShouldReturn403_WhenIsHuman() throws Exception {
        String humanDna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(humanDna))
            .andExpect(status().isForbidden())
            .andExpect(content().json("{\"message\": \"No es un mutante\"}"));
    }

    @Test
    void testPostMutant_ShouldReturn400_WhenDnaIsInvalid() throws Exception {
        String invalidDna = """
            {
              "dna": [
                "ATXCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidDna))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testPostMutant_ShouldReturn400_WhenDnaTooSmall() throws Exception {
        String smallDna = """
            {
              "dna": [
                "ATG",
                "CAG",
                "TTA"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(smallDna))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testPostMutant_ShouldReturn400_WhenDnaIsNotSquare() throws Exception {
        String nonSquareDna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTAT",
                "AGACGG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nonSquareDna))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(containsString("inválido")));
    }

    @Test
    void testPostMutant_ShouldNotSaveDuplicate_WhenSameDnaSentTwice() throws Exception {
        String mutantDna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutantDna))
            .andExpect(status().isOk());

        long countAfterFirst = dnaRepository.count();

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutantDna))
            .andExpect(status().isOk());

        long countAfterSecond = dnaRepository.count();

        assert countAfterFirst == countAfterSecond : "No debería duplicar el DNA";
    }

    @Test
    void testGetStats_ShouldReturnCorrectStats_WhenNoData() throws Exception {
        mockMvc.perform(get("/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count_mutant_dna").value(0))
            .andExpect(jsonPath("$.count_human_dna").value(0))
            .andExpect(jsonPath("$.ratio").value(0.0));
    }

    @Test
    void testGetStats_ShouldReturnCorrectStats_AfterSendingData() throws Exception {
        String mutantDna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutantDna))
            .andExpect(status().isOk());

        String humanDna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(humanDna))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count_mutant_dna").value(1))
            .andExpect(jsonPath("$.count_human_dna").value(1))
            .andExpect(jsonPath("$.ratio").value(1.0));
    }

    @Test
    void testGetStats_ShouldCalculateCorrectRatio() throws Exception {
        String mutant1 = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
              ]
            }
            """;

        String mutant2 = """
            {
              "dna": [
                "AAAATG",
                "TGCAGT",
                "GCTTTT",
                "CGATCT",
                "AGTACG",
                "TGACTA"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutant1))
            .andExpect(status().isOk());

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutant2))
            .andExpect(status().isOk());

        String human1 = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
              ]
            }
            """;

        String human2 = """
            {
              "dna": [
                "ATGCGA",
                "CAGTAC",
                "TTATCG",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
              ]
            }
            """;

        String human3 = """
            {
              "dna": [
                "AAAATG",
                "TGCAGT",
                "GCTTCT",
                "CGATCT",
                "AGTACG",
                "TGACTA"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(human1))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(human2))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(human3))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count_mutant_dna").value(2))
            .andExpect(jsonPath("$.count_human_dna").value(3))
            .andExpect(jsonPath("$.ratio").value(closeTo(0.67, 0.01)));
    }

    @Test
    void testGetHome_ShouldReturnHtmlPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Mutant Detection API")))
            .andExpect(content().string(containsString("swagger-ui")));
    }

    @Test
    void testPostMutant_ShouldReturn400_WhenDnaIsNull() throws Exception {
        String nullDna = """
            {
              "dna": null
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nullDna))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testPostMutant_ShouldReturn400_WhenDnaIsEmpty() throws Exception {
        String emptyDna = """
            {
              "dna": []
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyDna))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testPostMutant_ShouldReturn400_WhenJsonMalformed() throws Exception {
        String malformedJson = "{ this is not valid json }";

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testPostMutant_ShouldDetectHorizontalSequences() throws Exception {
        String dna = """
            {
              "dna": [
                "AAAATG",
                "TGCAGT",
                "GCTTTT",
                "CGATCT",
                "AGTACG",
                "TGACTA"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dna))
            .andExpect(status().isOk());
    }

    @Test
    void testPostMutant_ShouldDetectVerticalSequences() throws Exception {
        String dna = """
            {
              "dna": [
                "ATGCGA",
                "ATGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dna))
            .andExpect(status().isOk());
    }

    @Test
    void testPostMutant_ShouldDetectDiagonalSequences() throws Exception {
        String dna = """
            {
              "dna": [
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dna))
            .andExpect(status().isOk());
    }

    @Test
    void testPostMutant_ShouldReturnForbidden_WhenOnlyOneSequence() throws Exception {
        String dna = """
            {
              "dna": [
                "AAAATG",
                "TGCAGT",
                "GCTTCT",
                "CGATCT",
                "AGTACG",
                "TGACTA"
              ]
            }
            """;

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dna))
            .andExpect(status().isForbidden());
    }
}

