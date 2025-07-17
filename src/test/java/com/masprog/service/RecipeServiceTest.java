package com.masprog.service;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.model.Recipe;
import com.masprog.model.RecipeOrigin;
import com.masprog.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void shouldSaveRecipe_whenValidDTO_thenReturnResponseDTO() {
        // Entrada (DTO)
        RecipeDTO requestDTO = new RecipeDTO();
        requestDTO.setValue(BigDecimal.valueOf(1000));
        requestDTO.setOrigin(RecipeOrigin.SALARIO);
        requestDTO.setDestination("Banco BAI");
        requestDTO.setReceivedDate(LocalDate.of(2025, 7, 1));

        // Entidade que será salva
        Recipe recipeEntity = new Recipe();
        recipeEntity.setId(1L);
        recipeEntity.setValue(requestDTO.getValue());
        recipeEntity.setOrigin(requestDTO.getOrigin());
        recipeEntity.setDestination(requestDTO.getDestination());
        recipeEntity.setReceivedDate(requestDTO.getReceivedDate());
        recipeEntity.setCreatedAt(LocalDate.now());

        // Simula o comportamento do repository
        Mockito.when(recipeRepository.save(any(Recipe.class))).thenReturn(recipeEntity);

        // Executa o método
        RecipeResponseDTO responseDTO = recipeService.createRecipe(requestDTO);

        // Asserts
        assertNotNull(responseDTO);
        assertEquals(requestDTO.getValue(), responseDTO.getValue());
        assertEquals(requestDTO.getOrigin(), responseDTO.getOrigin());
        assertEquals(requestDTO.getDestination(), responseDTO.getDestination());
        assertEquals(requestDTO.getReceivedDate(), responseDTO.getReceivedDate());
    }
}
