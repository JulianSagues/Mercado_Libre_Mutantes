package org.example.mercadolibre.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mercadolibre.dto.DnaRequest;
import org.example.mercadolibre.dto.StatsResponse;
import org.example.mercadolibre.service.MutantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Tag(name = "Mutant Detection API", description = "API para detectar mutantes mediante análisis de ADN")
public class MutantController {

    @Autowired
    private MutantService mutantService;

    @PostMapping("/mutant")
    @Operation(
            summary = "Detectar si un ADN es mutante",
            description = "Recibe una secuencia de ADN y determina si pertenece a un mutante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Es un mutante",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Es un mutante\"}"))),
            @ApiResponse(responseCode = "403", description = "No es un mutante",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"No es un mutante\"}"))),
            @ApiResponse(responseCode = "400", description = "ADN inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"ADN inválido: debe ser una matriz NxN con solo caracteres A, T, C, G\"}")))
    })
    public ResponseEntity<?> isMutant(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DnaRequest.class),
                            examples = @ExampleObject(name = "Ejemplo ADN mutante",
                                    value = "{\n  \"dna\": [\n    \"ATGCGA\",\n    \"CAGTGC\",\n    \"TTATGT\",\n    \"AGAAGG\",\n    \"CCCCTA\",\n    \"TCACTG\"\n  ]\n}")
                    )
            ) DnaRequest request) {
        try {
            boolean isMutant = mutantService.analyzeDna(request.getDna());

            if (isMutant) {
                return ResponseEntity.ok().body("{\"message\": \"Es un mutante\"}");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"message\": \"No es un mutante\"}");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error interno del servidor\"}");
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas",
            description = "Devuelve estadísticas de las verificaciones de ADN")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StatsResponse.class),
                    examples = @ExampleObject(value = "{\n  \"count_mutant_dna\": 40,\n  \"count_human_dna\": 100,\n  \"ratio\": 0.4\n}")))
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = mutantService.getStats();
        return ResponseEntity.ok(stats);
    }
}
