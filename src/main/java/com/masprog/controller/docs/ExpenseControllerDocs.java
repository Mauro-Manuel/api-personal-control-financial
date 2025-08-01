package com.masprog.controller.docs;

import com.masprog.dto.ExpenseDTO;
import com.masprog.dto.ExpenseResponseDTO;
import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ExpenseControllerDocs {

    @Operation(summary = "Adicionar uma nova despesa",
            description = "Adicionar uma nova despesa",
            tags = {"Despesas"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ExpenseResponseDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ExpenseResponseDTO> create(ExpenseDTO expense);
}
