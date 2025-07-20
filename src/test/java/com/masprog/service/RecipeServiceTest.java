package com.masprog.service;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeFilterDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.model.Recipe;
import com.masprog.model.RecipeOrigin;
import com.masprog.repository.RecipeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipeEntity);

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

        when(recipeRepository.save(any(Recipe.class)))
                .thenThrow(new RuntimeException("Falha na base de dados"));

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            recipeService.createRecipe(dto);
        });

        assertEquals("Falha na base de dados", ex.getMessage());
    }

    @Test
    void testGetAllRecipesWithFilter(){
        RecipeFilterDTO filter = new RecipeFilterDTO();
        filter.setOrigin(RecipeOrigin.SALARIO);
        Pageable pageable = PageRequest.of(0, 10);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("5000.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 6,26));

        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        // Explicitly cast the first argument to Specification<Recipe>
        when(recipeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(recipePage);

        // Act
        Page<RecipeResponseDTO> result = recipeService.getAllRecipes(filter, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(RecipeOrigin.SALARIO, result.getContent().get(0).getOrigin());
    }




}
