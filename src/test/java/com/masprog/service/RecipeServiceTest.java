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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {


    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    @Test
    void shouldHaveViolation_whenValueIsNegative() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(BigDecimal.valueOf(-50));
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.of(2025, 7, 1));

        Set<ConstraintViolation<RecipeDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));
    }

    @Test
    void shouldHaveViolation_whenValueIsNull() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(null); // obrigatório
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.of(2025, 7, 1));

        Set<ConstraintViolation<RecipeDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldHaveViolation_whenReceivedDateIsInFuture() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(BigDecimal.valueOf(100));
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.now().plusDays(10)); // futura

        Set<ConstraintViolation<RecipeDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowRuntimeException_whenRepositoryFails() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(BigDecimal.valueOf(1000));
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.of(2025, 7, 1));

        Mockito.when(recipeRepository.save(any(Recipe.class)))
                .thenThrow(new RuntimeException("Falha na base de dados"));

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            recipeService.createRecipe(dto);
        });

        assertEquals("Falha na base de dados", ex.getMessage());
    }


}
